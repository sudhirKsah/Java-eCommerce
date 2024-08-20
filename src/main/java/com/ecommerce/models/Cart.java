package com.ecommerce.models;

import com.ecommerce.DatabaseManager;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Cart {

    private static final ObservableList<CartItem> items = FXCollections.observableArrayList();

    public static ObservableList<CartItem> getItems(int userId) {
        items.clear();
        
        String query = "SELECT products.product_id, products.name, products.price, cart_item.quantity " +
                       "FROM cart_item JOIN products ON cart_item.product_id = products.product_id " +
                       "WHERE cart_item.user_id = ?";
        try (Connection connection = DatabaseManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, userId);
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                int productId = resultSet.getInt("product_id");
                String name = resultSet.getString("name");
                double price = resultSet.getDouble("price");
                int quantity = resultSet.getInt("quantity");
                CartItem item = new CartItem(productId, userId, name, price, quantity);
                items.add(item);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return items;
    }

    public static void addItem(CartItem item) {
        String checkQuery = "SELECT quantity FROM cart_item WHERE user_id = ? AND product_id = ?";
        String updateQuery = "UPDATE cart_item SET quantity = quantity + ? WHERE user_id = ? AND product_id = ?";
        String insertQuery = "INSERT INTO cart_item (user_id, product_id, quantity) VALUES (?, ?, ?)";
        
        try (Connection connection = DatabaseManager.getConnection();
             PreparedStatement checkStatement = connection.prepareStatement(checkQuery)) {
            checkStatement.setInt(1, item.getUserId());
            checkStatement.setInt(2, item.getProductId());
            ResultSet resultSet = checkStatement.executeQuery();
            if (resultSet.next()) {
                try (PreparedStatement updateStatement = connection.prepareStatement(updateQuery)) {
                    updateStatement.setInt(1, item.getQuantity());
                    updateStatement.setInt(2, item.getUserId());
                    updateStatement.setInt(3, item.getProductId());
                    updateStatement.executeUpdate();
                }
            } else {
                try (PreparedStatement insertStatement = connection.prepareStatement(insertQuery)) {
                    insertStatement.setInt(1, item.getUserId());
                    insertStatement.setInt(2, item.getProductId());
                    insertStatement.setInt(3, item.getQuantity());
                    insertStatement.executeUpdate();
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void removeItem(CartItem item) {
        String query = "DELETE FROM cart_item WHERE user_id = ? AND product_id = ?";
        try (Connection connection = DatabaseManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, item.getUserId());
            statement.setInt(2, item.getProductId());
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void clear(int userId) {
        String query = "DELETE FROM cart_item WHERE user_id = ?";
        try (Connection connection = DatabaseManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, userId);
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
