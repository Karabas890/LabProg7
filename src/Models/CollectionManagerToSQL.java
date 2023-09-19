package Models;

import java.io.Serializable;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.*;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.LinkedList;

/**
 * Класс, управляющий коллекцией объектов класса SpaceMarine
 */
public class CollectionManagerToSQL extends CollectionManager implements Serializable {


    //region Поля

    //region БД

    /**
     * URL подключения к БД
     * Например: "jdbc:postgresql://localhost:5432/YOUR_DB_NAME"
     */
    private final String dbURL;
    /**
     * Имя пользователя БД
     * Например: "postgres"
     */
    private final String dbUsername;
    /**
     * пароль БД
     * Например "12345678"
     */
    private final String dbPassword;
    /**
     * Соединение с БД
     */
    private Connection connection;
    /**
     * ID пользователя, установившего соединение с БД,
     * его данные хранятся в таблице Users
     */
    private int idUser;
    //endregion

    //endregion

    //region Конструкторы

    /**
     * @param dbURL      URL подключения к БД,
     *                   Например: "jdbc:postgresql://localhost:5432/YOUR_DB_NAME"
     * @param dbUsername Имя пользователя БД
     *                   Например: "postgres"
     * @param dbPassword пароль БД
     *                   Например "12345678"
     * @param idUser     ID пользователя, установившего соединение с БД,
     *                   его данные хранятся в таблице Users
     * @throws Exception Исключение при создании соединения с сервером
     */
    public CollectionManagerToSQL(String dbURL, String dbUsername, String dbPassword, int idUser) throws Exception {
        executedScripts = new HashSet<>();
        marines = new LinkedList<>();
        initializationDate = LocalDate.now();
        commandHistory = new LinkedList<>();
        this.dbURL = dbURL;
        this.dbUsername = dbUsername;
        this.dbPassword = dbPassword;
        this.idUser = idUser;
        this.Connect();
        this.Load(this.idUser);
    }
    //endregion

    //region Сеттеры и геттеры
    public int getIdUser() {
        return idUser;
    }

    public void setIdUser(int idUser) {
        this.idUser = idUser;
    }
    //endregion

    //region Методы

    private String GetSHA224(String string) {
        try {
            // Create a MessageDigest instance with SHA-224 algorithm
            MessageDigest messageDigest = MessageDigest.getInstance("SHA-224");

            // Convert the password to bytes and hash it
            byte[] passwordBytes = string.getBytes();
            byte[] hashBytes = messageDigest.digest(passwordBytes);

            // Convert the hashed bytes to hexadecimal string
            StringBuilder hashedPassword = new StringBuilder();
            for (byte b : hashBytes) {
                hashedPassword.append(String.format("%02x", b));
            }

            return hashedPassword.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }

        return null;
    }


    //region Соединение с БД
    public void Connect() throws SQLException {
        this.Connect(dbURL, dbUsername, dbPassword);
    }

    public void Connect(String dataBaseURL, String login, String password) throws SQLException {
        this.connection = DriverManager.getConnection(dataBaseURL, login, password);
    }
    //endregion

    //region Загрузка данных из БД

    /**
     * Возвращает объект Chapter по заданному идентификатору из базы данных.
     *
     * @param id идентификатор главы
     * @return объект Chapter или null, если глава не найдена
     * @throws Exception если произошла ошибка при выполнении запроса к базе данных
     */
    private Chapter GetChapter(int id) throws Exception {
        try (PreparedStatement statement = connection.prepareStatement("SELECT * FROM Chapters WHERE id = ?")) {
            statement.setInt(1, id);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                String name = resultSet.getString("title");
                String parentLegion = resultSet.getString("parent_legion");
                return new Chapter(id, name, parentLegion);
            } else {
                return null;
            }
        }
    }

    /**
     * Возвращает объект Coordinate по заданному идентификатору из базы данных.
     *
     * @param id идентификатор координат
     * @return объект Coordinate или null, если координаты не найдены
     * @throws Exception если произошла ошибка при выполнении запроса к базе данных
     */
    private Coordinate GetCoordinate(int id) throws Exception {
        try (PreparedStatement statement = connection.prepareStatement("SELECT * FROM Coordinates WHERE id = ?")) {
            statement.setInt(1, id);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                float x = resultSet.getFloat("x");
                int y = resultSet.getInt("y");
                return new Coordinate(id, x, y);
            } else {
                return null;
            }
        }
    }

    public User GetUser(int idUser) throws Exception {
        User user = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;

        if (connection == null) {
            throw new Exception("Не установлено подключение к серверу!");
        }

        // Prepare the SQL statement
        String sql = "SELECT * FROM Users WHERE id = ? ";
        statement = this.connection.prepareStatement(sql);
        statement.setInt(1, idUser);
        // Start the query
        resultSet = statement.executeQuery();

        // Check if a user is found
        if (resultSet.next()) {
            int userId = resultSet.getInt("id");
            String foundLogin = resultSet.getString("Login");
            String foundPassword = resultSet.getString("password");

            // Create a User object
            user = new User(userId, foundLogin, foundPassword);
        }

        // Close the resources
        resultSet.close();
        statement.close();

        return user;
    }

    public User GetUser(String login, String password) throws Exception {
        User user = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;

        if (connection == null) {
            throw new Exception("Не установлено подключение к серверу!");
        }

        // Hash the password using SHA-224 algorithm
        String hashedPassword = GetSHA224(password);

        // Prepare the SQL statement
        String sql = "SELECT * FROM Users WHERE login = ? AND password = ?";
        statement = this.connection.prepareStatement(sql);
        statement.setString(1, login);
        statement.setString(2, hashedPassword);

        // Start the query
        resultSet = statement.executeQuery();

        // Check if a user is found
        if (resultSet.next()) {
            int userId = resultSet.getInt("id");
            String foundLogin = resultSet.getString("Login");
            String foundPassword = resultSet.getString("password");

            // Create a User object
            user = new User(userId, foundLogin, foundPassword);
        }

        // Close the resources
        resultSet.close();
        statement.close();

        return user;
    }

    public Weapon GetWeaponTypeById(int id) throws SQLException {
        String query = "SELECT * FROM WeaponTypes WHERE id = ?";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, id);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                String title = resultSet.getString("title");
                return new Weapon(id, title);
            }
        } catch (Exception e) {
            System.err.println("Не удалось загрузить WeaponType из БД: " + e.getMessage());
        }
        return null;
    }

    public Astartes GetAstartesCategoryById(int id) throws SQLException {
        String query = "SELECT * FROM Categories WHERE id = ?";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, id);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                String title = resultSet.getString("title");
                return new Astartes(id, title);
            }
        } catch (Exception e) {
            System.err.println("Не удалось загрузить Category из БД: " + e.getMessage());
        }
        return null;
    }

    /**
     * @param filePath
     * @return
     * @throws Exception
     */
    public boolean Load(String filePath) throws Exception {
        return false;
    }

    public boolean Load() throws SQLException {
        this.clear();
        String query = "SELECT * FROM Marines";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            this.clear();
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                int idMarine = resultSet.getInt("id");
                this.add(new SpaceMarine(
                        idMarine,
                        resultSet.getInt("idUser"),
                        resultSet.getString("name"),
                        GetCoordinate(resultSet.getInt("idCoordinate")),
                        resultSet.getTimestamp("creationDate"),
                        resultSet.getInt("health"),
                        resultSet.getString("achievements"),
                        GetAstartesCategoryById(resultSet.getInt("idCategory")),
                        GetWeaponTypeById(resultSet.getInt("idWeaponType")),
                        GetChapter(resultSet.getInt("idChapter"))
                ));
            }
            return true;
        } catch (Exception e) {
            System.err.println("Не удалось загрузить данные из БД: " + e.getMessage());
            return false;
        }
    }


    public void Load(int idUser) throws SQLException {
        String query = "SELECT * FROM Marines WHERE idUser = ?";

        query = "SELECT * FROM Marines WHERE idUser = ?";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, idUser);
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                marines.add(new SpaceMarine(
                        resultSet.getInt("id"),
                        idUser,
                        resultSet.getString("name"),
                        GetCoordinate(resultSet.getInt("idCoordinate")),
                        resultSet.getTimestamp("creationDate"),
                        resultSet.getInt("health"),
                        resultSet.getString("achievements"),
                        GetAstartesCategoryById(resultSet.getInt("idCategory")),
                        GetWeaponTypeById(resultSet.getInt("idWeaponType")),
                        GetChapter(resultSet.getInt("idChapter"))
                ));
            }
        } catch (Exception e) {
            System.err.println("Не удалось загрузить данные из БД: " + e.getMessage());
        }
    }
    //endregion

    //region Сохранение данных в БД

    public int saveCoordinate(Coordinate coordinate) throws SQLException {
        String query = "INSERT INTO Coordinates (x, y) VALUES (?, ?) ON CONFLICT (x, y) DO UPDATE SET x = Coordinates.x, y = Coordinates.y RETURNING id";

        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setFloat(1, coordinate.getX());
            statement.setInt(2, coordinate.getY());
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                return resultSet.getInt("id");
            }
        } catch (Exception e) {
            System.err.println("Не удалось сохранить координаты в БД: " + e.getMessage());
        }
        return 0;
    }

    public int saveWeapon(Weapon weapon) throws SQLException {
        String query = "INSERT INTO WeaponTypes (title) VALUES (?) ON CONFLICT (title) DO UPDATE SET title = WeaponTypes.title RETURNING id";

        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, weapon.getTitle());
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                return resultSet.getInt("id");
            }
        } catch (Exception e) {
            System.err.println("Не удалось сохранить тип оружия в БД: " + e.getMessage());
        }
        return 0;
    }

    public int saveAstartes(Astartes astartes) throws SQLException {
        String query = "INSERT INTO Categories (title) VALUES (?) ON CONFLICT (title) DO UPDATE SET title = Categories.title RETURNING id";

        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, astartes.getTitle());
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                return resultSet.getInt("id");
            }
        } catch (Exception e) {
            System.err.println("Не удалось сохранить категорию Astartes в БД: " + e.getMessage());
        }
        return 0;
    }

    public int saveChapter(Chapter chapter) throws SQLException {
        String query = "INSERT INTO Chapters (title, parent_legion) VALUES (?, ?) RETURNING id";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, chapter.getName());
            statement.setString(2, chapter.getParentLegion());
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                return resultSet.getInt("id");
            }
        } catch (Exception e) {
            System.err.println("Не удалось сохранить главу в БД: " + e.getMessage());
        }
        return 0;
    }


    /**
     * @param filePath
     * @return
     * @throws Exception
     */
    public boolean Save(String filePath) throws Exception {
        return false;
    }

    public boolean Save() throws SQLException {
        if (marines.isEmpty()) {
            String cleanTableQuery = "TRUNCATE TABLE Marines";
            try (PreparedStatement statement = connection.prepareStatement(cleanTableQuery)) {
                statement.executeUpdate();
            } catch (Exception e) {
                System.err.println("Не удалось очистить таблицу Marines: " + e.getMessage());
            }
        } else {
            String query = "INSERT INTO Marines (id, name, idCoordinate, creationDate, health, achievements, idCategory, idWeaponType, idChapter,idUser) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?) " +
                    "ON CONFLICT (id) DO UPDATE " +
                    "SET name = EXCLUDED.name, " +
                    "idCoordinate = EXCLUDED.idCoordinate, " +
                    "creationDate = EXCLUDED.creationDate, " +
                    "health = EXCLUDED.health, " +
                    "achievements = EXCLUDED.achievements, " +
                    "idCategory = EXCLUDED.idCategory, " +
                    "idWeaponType = EXCLUDED.idWeaponType, " +
                    "idChapter = EXCLUDED.idChapter," +
                    "idUser = EXCLUDED.idUser";
            try (PreparedStatement statement = connection.prepareStatement(query)) {
                for (SpaceMarine marine : marines) {
                    statement.setInt(1, marine.getId());
                    statement.setString(2, marine.getName());
                    statement.setInt(3, this.saveCoordinate(marine.getCoordinates()));
                    statement.setTimestamp(4, marine.getCreationDate());
                    statement.setInt(5, marine.getHealth());
                    statement.setString(6, marine.getAchievements());
                    statement.setInt(7, this.saveAstartes(marine.getAstartes()));
                    statement.setInt(8, this.saveWeapon(marine.getWeapon()));
                    statement.setInt(9, this.saveChapter(marine.getChapter()));
                    statement.setInt(10, marine.getIdUser());
                    statement.executeUpdate();
                }
                return true;
            } catch (Exception e) {
                System.err.println("Не удалось сохранить данные в таблицу Marines: " + e.getMessage());
                return false;
            }
        }
        return true;
    }
    public int AddToBd(SpaceMarine marine) throws Exception {
        String query = "INSERT INTO Marines (id, name, idCoordinate, creationDate, health, achievements, idCategory, idWeaponType, idChapter,idUser) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?) " +
                "ON CONFLICT (id) DO UPDATE " +
                "SET name = EXCLUDED.name, " +
                "idCoordinate = EXCLUDED.idCoordinate, " +
                "creationDate = EXCLUDED.creationDate, " +
                "health = EXCLUDED.health, " +
                "achievements = EXCLUDED.achievements, " +
                "idCategory = EXCLUDED.idCategory, " +
                "idWeaponType = EXCLUDED.idWeaponType, " +
                "idChapter = EXCLUDED.idChapter," +
                "idUser = EXCLUDED.idUser";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
                statement.setInt(1, marine.getId());
                statement.setString(2, marine.getName());
                statement.setInt(3, this.saveCoordinate(marine.getCoordinates()));
                statement.setTimestamp(4, marine.getCreationDate());
                statement.setInt(5, marine.getHealth());
                statement.setString(6, marine.getAchievements());
                statement.setInt(7, this.saveAstartes(marine.getAstartes()));
                statement.setInt(8, this.saveWeapon(marine.getWeapon()));
                statement.setInt(9, this.saveChapter(marine.getChapter()));
                statement.setInt(10, marine.getIdUser());
           //    ResultSet result= statement.executeQuery();
             //  result.next();
            //    return result.getInt(1);
            statement.executeUpdate();
            return 0;

        } catch (Exception e) {
            System.err.println("Не удалось добавить данные в таблицу Marines: " + e.getMessage());
            return -1;
        }
    }
    public  boolean ClearBd(int userId)throws Exception{
        String query = "DELETE FROM marines where iduser=?";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1,userId);
            statement.executeUpdate();
            return true;
        }catch (Exception e){
            System.out.println("Не удалось очистить БД");
            return false;
        }
    }
    public  boolean RevomeByIdBd(int userId,int spacemarineid)throws Exception{
        String query = "DELETE FROM marines where id=? and iduser=?";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1,spacemarineid);
            statement.setInt(2,userId);
            if(statement.executeUpdate()==0){
                return false;
            }else {
                return true;
            }
        }catch (Exception e){
            System.out.println("Не удалось удалить спейсмарина по ИД");
            return false;
        }
    }
    public  boolean RevomeHeadBd(int userId)throws Exception{
        String query = "DELETE FROM marines WHERE iduser=? AND id = (SELECT MIN(id) FROM marines WHERE iduser=?)";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1,userId);
            statement.setInt(2,userId);
            if(statement.executeUpdate()==0){
                return false;
            }else {
                return true;
            }
        }catch (Exception e){
            System.out.println("Не удалось удалить спейсмарина по ИД");
            return false;
        }
    }
    public  boolean UpdateBd(int userId,int marineID,SpaceMarine marine)throws Exception{
        String query = "UPDATE marines SET " +
                "name = ?, " +
                "idCoordinate = ?, " +
                "creationDate = ?, " +
                "health = ?, " +
                "achievements = ?, " +
                "idCategory = ?, " +
                "idWeaponType = ?, " +
                "idChapter = ? " +
                "WHERE id = ? AND iduser = ?";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, marine.getName());
            statement.setInt(2, this.saveCoordinate(marine.getCoordinates()));
            statement.setTimestamp(3, marine.getCreationDate());
            statement.setInt(4, marine.getHealth());
            statement.setString(5, marine.getAchievements());
            statement.setInt(6, this.saveAstartes(marine.getAstartes()));
            statement.setInt(7, this.saveWeapon(marine.getWeapon()));
            statement.setInt(8, this.saveChapter(marine.getChapter()));
            statement.setInt(9, marineID);
            statement.setInt(10, userId);
             int count=statement.executeUpdate();
             if(count>0){
                 return true;
             }else{
                 return false;
            }
        }catch (Exception e){
            System.out.println("Не удалось обновить объект в БД");
            return false;
        }


    }

    /**
     * Аутентифицирует пользователя по заданному логину и паролю.
     *
     * @param login    Логин пользователя.
     * @param password Пароль пользователя.
     * @return Аутентифицированный пользователь.
     * @throws Exception Если произошла ошибка в процессе аутентификации.
     */
    @Override
    public User Login(String login, String password) throws Exception {
        User user = GetUser(login, password);
        if (user == null) {
            throw new Exception("Пользователь не найден!");
        }
        return user;
    }

    /**
     * Регистрирует нового пользователя с заданным логином и паролем.
     *
     * @param login    Логин пользователя.
     * @param password Пароль пользователя.
     * @return Зарегистрированный пользователь.
     * @throws Exception Если произошла ошибка в процессе регистрации.
     */
    @Override
    public User Register(String login, String password) throws Exception {
        User user = null;
        PreparedStatement statement = null;

        if (connection == null) {
            throw new Exception("Не установлено подключение к серверу!");
        }

        // Hash the password using SHA-224 algorithm
        String hashedPassword = GetSHA224(password);

        // Prepare the SQL statement
        String sql = "INSERT INTO Users (login, password) VALUES (?, ?)";
        statement = connection.prepareStatement(sql);
        statement.setString(1, login);
        statement.setString(2, hashedPassword);

        // Start the query
        int rowsAffected = statement.executeUpdate();

        if (rowsAffected > 0) {
            // Get the generated user ID
            ResultSet generatedKeys = statement.getGeneratedKeys();
            if (generatedKeys.next()) {
                int userId = generatedKeys.getInt(1);
                // Create a new User object
                user = new User(userId, login, hashedPassword);
            }
        }

        // Close the resources
        if (statement != null) {
            statement.close();
        }
        if (connection != null) {
            connection.close();
        }

        return user;
    }


    //endregion

    //endregion

}
