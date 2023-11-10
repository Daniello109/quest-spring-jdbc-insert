package com.wildcodeschool.wildandwizard.repository;

import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import com.wildcodeschool.wildandwizard.entity.Wizard;

import org.springframework.stereotype.Repository;
import util.JdbcUtils;

@Repository
public class WizardRepository {

    private final static String DB_URL = "jdbc:mysql://localhost:3306/spring_jdbc_quest?serverTimezone=GMT";
    private final static String DB_USER = "h4rryp0tt3r";
    private final static String DB_PASSWORD = "Horcrux4life!";

    public Wizard save(String firstName, String lastName, Date birthday, /*aquí vamos a crear el método save para insertar en la base de datos un wizard, pasádole en los parámetros los atributos que intruciremos en el formulario
    falta el id porque se va a generar automáticamente con generatedKeys*/
                       String birthPlace, String biography, boolean muggle) {

        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet generatedKeys = null;
        try {
            connection = DriverManager.getConnection(
                    DB_URL, DB_USER, DB_PASSWORD
            );
            statement = connection.prepareStatement(
                    "INSERT INTO wizard (first_name, last_name, birthday, birth_place, biography, is_muggle) VALUES (?, ?, ?, ?, ?, ?)",
                    Statement.RETURN_GENERATED_KEYS /*este statement devolverá el id creado en la base de datos*/
            );
            statement.setString(1, firstName);
            statement.setString(2, lastName);
            statement.setDate(3, birthday);
            statement.setString(4, birthPlace);
            statement.setString(5, biography);
            statement.setBoolean(6, muggle);

            if (statement.executeUpdate() != 1) { /*este método ejecutará el query. Devolverá las tuplas que se creen en la base de datos. Si este número es diferente a 1, no habrán funcionado y dará error*/
                throw new SQLException("failed to insert data");
            }

            generatedKeys = statement.getGeneratedKeys(); /*esto sirve para recuperar el id devuelto por  Statement.RETURN_GENERATED_KEYS*/

            if (generatedKeys.next()) { /*si no recupera ningún resultado, o sea una id, dará error*/
                Long id = generatedKeys.getLong(1); /*aquí afecta la id que recuperará y en la siguiete linea devolverá el wizard con todos los atributos para insertarlo en la tabla*/
                return new Wizard(id, firstName, lastName, birthday,
                        birthPlace, biography, muggle);
            } else {
                throw new SQLException("failed to get inserted id");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            JdbcUtils.closeResultSet(generatedKeys);
            JdbcUtils.closeStatement(statement);
            JdbcUtils.closeConnection(connection);
        }
        return null;
    }
}
