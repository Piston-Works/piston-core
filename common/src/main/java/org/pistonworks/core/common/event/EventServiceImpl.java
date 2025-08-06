package org.pistonworks.core.common.event;

import org.pistonworks.core.api.event.*;
import org.pistonworks.core.api.service.EventService;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Core event service implementation that handles event registration and firing.
 * This implementation is platform-agnostic and can be used across different implementations.
 */
public class EventServiceImpl implements EventService
{

    private final Map<Class<? extends Event>, List<RegisteredListener>> listeners = new ConcurrentHashMap<>();
    private final Map<Object, List<RegisteredListener>> listenersByObject = new ConcurrentHashMap<>();

    @Override
    public void registerListener(Object listener)
    {
        Class<?> clazz = listener.getClass();
        List<RegisteredListener> registeredListeners = new ArrayList<>();

        for (Method method : clazz.getMethods())
        {
            if (!method.isAnnotationPresent(EventHandler.class))
            {
                continue;
            }

            if (method.getParameterCount() != 1)
            {
                throw new IllegalArgumentException(
                        String.format("Event handler method %s.%s must have exactly one parameter",
                                clazz.getSimpleName(), method.getName()));
            }

            Class<?> eventClass = method.getParameterTypes()[0];
            if (!Event.class.isAssignableFrom(eventClass))
            {
                throw new IllegalArgumentException(
                        String.format("Event handler method %s.%s parameter must extend Event",
                                clazz.getSimpleName(), method.getName()));
            }

            EventHandler annotation = method.getAnnotation(EventHandler.class);
            RegisteredListener registeredListener = new RegisteredListener(
                    listener, method, annotation.priority(), annotation.ignoreCancelled());

            registeredListeners.add(registeredListener);

            @SuppressWarnings("unchecked")
            Class<? extends Event> eventType = (Class<? extends Event>) eventClass;
            listeners.computeIfAbsent(eventType, k -> new CopyOnWriteArrayList<>()).add(registeredListener);
        }

        if (!registeredListeners.isEmpty())
        {
            listenersByObject.put(listener, registeredListeners);
            sortListeners();
        }
    }

    @Override
    public void unregisterListener(Object listener)
    {
        List<RegisteredListener> registeredListeners = listenersByObject.remove(listener);
        if (registeredListeners != null)
        {
            for (RegisteredListener registeredListener : registeredListeners)
            {
                for (List<RegisteredListener> eventListeners : listeners.values())
                {
                    eventListeners.remove(registeredListener);
                }
            }
        }
    }

    @Override
    public <T extends Event> void registerListener(Class<T> eventClass, EventListener<T> listener)
    {
        RegisteredListener registeredListener = new FunctionalRegisteredListener<>(listener, EventPriority.NORMAL);
        listeners.computeIfAbsent(eventClass, k -> new CopyOnWriteArrayList<>()).add(registeredListener);
        sortListeners();
    }

    @Override
    public <T extends Event> T fireEvent(T event)
    {
        Class<? extends Event> eventClass = event.getClass();
        List<RegisteredListener> eventListeners = getListenersForEvent(eventClass);

        for (RegisteredListener listener : eventListeners)
        {
            if (event instanceof Cancellable)
            {
                Cancellable cancellable = (Cancellable) event;
                if (cancellable.isCancelled() && listener.isIgnoreCancelled())
                {
                    continue;
                }
            }

            try
            {
                listener.call(event);
            } catch (Exception e)
            {
                handleListenerException(listener, event, e);
            }
        }

        return event;
    }

    @Override
    public <T extends Event> CompletableFuture<T> fireEventAsync(T event)
    {
        return CompletableFuture.supplyAsync(() -> fireEvent(event));
    }

    @Override
    public int getListenerCount(Class<? extends Event> eventClass)
    {
        List<RegisteredListener> eventListeners = listeners.get(eventClass);
        return eventListeners != null ? eventListeners.size() : 0;
    }

    @Override
    public void unregisterAll()
    {
        listeners.clear();
        listenersByObject.clear();
    }

    private List<RegisteredListener> getListenersForEvent(Class<? extends Event> eventClass)
    {
        List<RegisteredListener> result = new ArrayList<>();

        // Add direct listeners
        List<RegisteredListener> directListeners = listeners.get(eventClass);
        if (directListeners != null)
        {
            result.addAll(directListeners);
        }

        // Add listeners for parent classes/interfaces
        for (Map.Entry<Class<? extends Event>, List<RegisteredListener>> entry : listeners.entrySet())
        {
            if (entry.getKey().isAssignableFrom(eventClass) && !entry.getKey().equals(eventClass))
            {
                result.addAll(entry.getValue());
            }
        }

        // Sort by priority
        result.sort((a, b) -> Integer.compare(b.getPriority().getValue(), a.getPriority().getValue()));

        return result;
    }

    private void sortListeners()
    {
        for (List<RegisteredListener> eventListeners : listeners.values())
        {
            eventListeners.sort((a, b) -> Integer.compare(b.getPriority().getValue(), a.getPriority().getValue()));
        }
    }

    /**
     * Handles exceptions thrown by event listeners.
     * Subclasses can override this to provide platform-specific error handling.
     */
    protected void handleListenerException(RegisteredListener listener, Event event, Exception exception)
    {
        System.err.printf("Error in event listener %s handling event %s: %s%n",
                listener.getClass().getSimpleName(), event.getEventName(), exception.getMessage());
        exception.printStackTrace();
    }
}
