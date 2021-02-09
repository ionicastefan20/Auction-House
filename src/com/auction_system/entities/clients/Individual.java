package com.auction_system.entities.clients;

import lombok.EqualsAndHashCode;
import lombok.Getter;

/**
 * The individual variant of the client
 */
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = true)
public class Individual extends Client {

    /**
     * The birth date of the individual
     */
    @Getter
    private String birthDate;

    /**
     * Private constructor used by the builder
     * @param username the username
     */
    private Individual(String username) {
        super(username);
    }

    /**
     * Builder for the individual class
     */
    public static class IndividualBuilder extends Client.ClientBuilder {

        /**
         * The instance of the individual
         */
        private final Individual individual;

        /**
         * Constructor for the builder
         * @param username the username of the individual
         */
        public IndividualBuilder(String username) {
            this.client = new Individual(username);
            this.individual = (Individual) client;
        }

        /**
         * Adds the birth date
         * @param birthDate the birth date
         * @return the builder
         */
        public IndividualBuilder withBirthDate(String birthDate) {
            individual.birthDate = birthDate;
            return this;
        }
    }
}
