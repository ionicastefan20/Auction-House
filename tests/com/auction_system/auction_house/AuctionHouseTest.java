package com.auction_system.auction_house;

import com.auction_system.entities.clients.Client;
import com.auction_system.entities.employees.Administrator;
import com.auction_system.entities.employees.Broker;
import com.auction_system.exceptions.*;
import com.auction_system.products.Product;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.junit.Test;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class AuctionHouseTest {

    @Test
    public void maxBid() {
        List<Pair<String, Double>> bids = new ArrayList<>(4);
        bids.add(new ImmutablePair<>("a", 8210.0));
        bids.add(new ImmutablePair<>("b", 7890.0));
        bids.add(new ImmutablePair<>("c", 8130.0));
        bids.add(new ImmutablePair<>("d", 8090.0));

        Pair<String, Double> result = RealAuctionHouse.getInstance().getMax(bids);
        Pair<String, Double> expected = new ImmutablePair<>("a", 8210.0);

        assertEquals(result, expected);
    }

    @Test
    public void removeProduct() throws SQLException, InvalidOptionException {
        new Administrator("admin", DigestUtils.sha3_512Hex("admin")).loadProducts();
        RealAuctionHouse.getInstance().getProduct(102).setSalePrice(8000);
        RealAuctionHouse.getInstance().removeProduct(102);
        Product p = RealAuctionHouse.getInstance().getProduct(102);

        assertEquals(p, null);
    }

    @Test(expected = NoBrokersException.class)
    public void noBrokersExceptionTest() throws SQLException, MyException {
        new Administrator("admin", DigestUtils.sha3_512Hex("admin")).loadProducts();
        RealAuctionHouse.getInstance().offerInit("naomi6",101, 8000, 8);
    }

    @Test(expected = ProductDoesNotExistException.class)
    public void productDoesNotExistExceptionTest() throws SQLException, MyException {
        new Administrator("admin", DigestUtils.sha3_512Hex("admin")).loadProducts();
        Broker.login("mihai8", DigestUtils.sha3_512Hex("mihai8"));
        RealAuctionHouse.getInstance().offerInit("naomi6",401, 8000, 8);
    }

    @Test(expected = BidOnTheSameProductException.class)
    public void bidOnTheSameProductExceptionTest() throws SQLException, MyException {
        new Administrator("admin", DigestUtils.sha3_512Hex("admin")).loadProducts();
        Broker.login("mihai8", DigestUtils.sha3_512Hex("mihai8"));
        Client.login("naomi6", DigestUtils.sha3_512Hex("naomi6"));
        RealAuctionHouse.getInstance().offerInit("naomi6",101, 8000, 8);
        RealAuctionHouse.getInstance().offerInit("naomi6",101, 7800, 8);
    }

    @Test(expected = MaxBidsNumberException.class)
    public void maxBidsNumberExceptionTest() throws SQLException, MyException {
        new Administrator("admin", DigestUtils.sha3_512Hex("admin")).loadProducts();
        Broker.login("mihai8", DigestUtils.sha3_512Hex("mihai8"));
        Client.login("naomi6", DigestUtils.sha3_512Hex("naomi6"));
        Client.login("barry02", DigestUtils.sha3_512Hex("barry02"));
        Client.login("mariko", DigestUtils.sha3_512Hex("mariko"));
        Client.login("linus", DigestUtils.sha3_512Hex("linus"));
        RealAuctionHouse.getInstance().offerInit("naomi6",101, 8000, 8);
        RealAuctionHouse.getInstance().offerInit("barry02",101, 7800, 7);
        RealAuctionHouse.getInstance().offerInit("mariko",101, 7900, 9);
        RealAuctionHouse.getInstance().offerInit("linus",101, 8100, 6);
    }

    @Test(expected = WrongPasswordException.class)
    public void clientWrongPasswordExceptionTest() throws SQLException, MyException {
        Client.login("naomi6", DigestUtils.sha3_512Hex("wrongPass"));
    }

    @Test(expected = UserAlreadyLoggedInException.class)
    public void userAlreadyLoggedInExceptionTest() throws SQLException, MyException {
        Client.login("naomi6", DigestUtils.sha3_512Hex("naomi6"));
        Client.login("naomi6", DigestUtils.sha3_512Hex("naomi6"));
    }
}
