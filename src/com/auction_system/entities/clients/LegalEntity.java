package com.auction_system.entities.clients;

import lombok.Getter;

@Getter
public class LegalEntity extends Client {
    public enum Company {
        SRL,
        SA
    }

    private Company company;

    private double shareCapital;

    private LegalEntity(String username) {
        super(username);
    }

    public static class LegalEntityBuilder extends Client.ClientBuilder {

        private final LegalEntity legalEntity;

        public LegalEntityBuilder(String username) {
            this.client = new LegalEntity(username);
            this.legalEntity = (LegalEntity) client;
        }

        public LegalEntityBuilder withCompany(Company company) {
            legalEntity.company = company;
            return this;
        }

        public LegalEntityBuilder withShareCapital(double shareCapital) {
            legalEntity.shareCapital = shareCapital;
            return this;
        }
    }
}
