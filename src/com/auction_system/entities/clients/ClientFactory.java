package com.auction_system.entities.clients;

/**
 * Factory for the client
 */
public class ClientFactory {

    /**
     * Private constructor
     */
    private ClientFactory() {
    }

    /**
     * Returns a client based on the provided type or null if the type is invalid
     * @param type the type of the client
     * @param data the data used to create the client
     * @return the client
     */
    public static Client getClient(String type, String[] data) {
        Client.ClientBuilder builder = null;

        if ("individual".equalsIgnoreCase(type))
            builder = new Individual.IndividualBuilder(data[0])
                    .withBirthDate(data[3]);
        else if ("legalEntity".equalsIgnoreCase(type))
            builder = new LegalEntity.LegalEntityBuilder(data[0])
                    .withCompany(LegalEntity.Company.valueOf(data[3]))
                    .withShareCapital(Double.parseDouble(data[4]));
        else
            return null;

        return builder.withName(data[1])
                .withAddress(data[2])
                .build();
    }
}
