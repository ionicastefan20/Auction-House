package com.testing;

import com.auction_system.auction_house.BrokerAHProxy;
import com.auction_system.auction_house.ClientAHProxy;
import com.auction_system.entities.clients.Client;
import com.auction_system.entities.clients.Individual;
import com.auction_system.entities.employees.Administrator;
import com.auction_system.entities.employees.Broker;
import com.auction_system.exceptions.MyException;
import com.auction_system.exceptions.UserAlreadyExistsException;
import com.auction_system.exceptions.UserDoesNotExistException;
import com.auction_system.exceptions.WrongPasswordException;
import org.apache.commons.codec.digest.DigestUtils;
import org.junit.Test;

import java.sql.SQLException;

public class AuctionTest {
    private Client barry, naomi, karen, nixon;
    private Broker radu, mihai;
    private Administrator admin;

    public void loginAdmin() throws SQLException {
        admin = new Administrator("admin", DigestUtils.sha3_512Hex("admin"));
    }

    @Test
    public void registerClient() throws MyException, SQLException {
        Client naomi = new Individual.IndividualBuilder("nixony")
                .withName("Hall Nixon")
                .withAddress("P.O. Box 498, 8571 Duis Street, Luxembourg")
                .asIndividualBuilder()
                .withBirthDate("27/07/1989")
                .build();
        new ClientAHProxy().registerClient(naomi, DigestUtils.sha3_512Hex("nixony"));
    }

    @Test
    public void loginClients() throws MyException, SQLException {
        barry = Client.login("barry02", DigestUtils.sha3_512Hex("barry02"));
        naomi = Client.login("naomi6", DigestUtils.sha3_512Hex("naomi6"));
        karen = Client.login("karenBitch", DigestUtils.sha3_512Hex("karenBitch"));
        nixon = Client.login("nixony", DigestUtils.sha3_512Hex("nixony"));
    }

    @Test
    public void registerBroker() throws MyException, SQLException {
        new BrokerAHProxy().registerBroker(new Broker("radu07"), DigestUtils.sha3_512Hex("radu07"));
        new BrokerAHProxy().registerBroker(new Broker("mihai8"), DigestUtils.sha3_512Hex("mihai8"));
    }

    @Test
    public void loginBrokers() throws MyException, SQLException {
        radu = Broker.login("radu07", DigestUtils.sha3_512Hex("radu07"));
        mihai = Broker.login("mihai8", DigestUtils.sha3_512Hex("mihai8"));
    }

    @Test
    public void loadProducts() throws SQLException {
        admin.getAuctionHouse().loadProducts(admin.getConn());
        admin.getAuctionHouse().getProducts().forEach(System.out::println);
    }

    @Test
    public void auctionTest() {
        try {
            loginAdmin();
            loadProducts();
            loginBrokers();
            loginClients();

            barry.getAuctionHouse().offerInit(barry.getUsername(), 101, 7000, 3);
            naomi.getAuctionHouse().offerInit(naomi.getUsername(), 101, 8200, 7);
            karen.getAuctionHouse().offerInit(karen.getUsername(), 101, 7800, 8);;
            nixon.getAuctionHouse().offerInit(nixon.getUsername(), 101, 6900, 1);

            admin.getAuctionHouse().stopExecutor();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        new AuctionTest().auctionTest();
    }
}
