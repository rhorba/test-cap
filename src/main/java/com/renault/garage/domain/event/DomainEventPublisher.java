package com.renault.garage.domain.event;

/**
 * Interface pour publier les événements domaine
 * Abstraction pour découpler le domaine de l'infrastructure
 */
public interface DomainEventPublisher {
    
    /**
     * Publie un événement domaine
     * @param event L'événement à publier
     */
    void publish(Object event);
}
