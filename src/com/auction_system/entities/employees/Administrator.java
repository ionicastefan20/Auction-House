package com.auction_system.entities.employees;

import com.auction_system.auction_house.AdminAHProxy;
import com.auction_system.auction_house.BrokerAHProxy;
import com.auction_system.auction_house.IAdminAH;
import com.auction_system.database_system.SqlUtility;
import com.auction_system.exceptions.MyException;
import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.sql.Connection;
import java.sql.SQLException;

@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Administrator implements IEmployee {

    @EqualsAndHashCode.Include
    private final String username;
    @Getter
    private final Connection conn;
    @Getter
    private final IAdminAH auctionHouse;

    public Administrator(String username, String password) throws SQLException {
        // TODO encrypt the fucking password
        this.username = username;
        conn = SqlUtility.connect(username, password);
        auctionHouse = new AdminAHProxy();
    }

    public static void registerBroker(Broker broker, String hash) throws MyException, SQLException {
        new BrokerAHProxy().registerBroker(broker, hash);
    }

    public void closeConn() throws SQLException {
        conn.close();
    }
}
