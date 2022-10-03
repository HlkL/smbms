package com.hg.service.user;

import com.hg.dao.BaseDao;
import com.hg.dao.user.UserDao;
import com.hg.dao.user.UserDaoImpl;
import com.hg.pojo.User;
import org.junit.Test;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

/**
 * @program: smbms
 * @description: service层捕获异常，进行事务处理
 * 事务处理：调用不同dao的多个方法，必须使用同一个connection（connection作为参数传递）
 * 事务完成之后，需要在service层进行connection的关闭，在dao层关闭（PreparedStatement和ResultSet对象）
 * @author: hougen
 * @create: 2022-09-23 13:43
 */
public class UserServiceImpl implements UserService {
    /**
     * 在业务层调用Dao层
     */
    private UserDao userDao;

    public UserServiceImpl() {
        this.userDao = new UserDaoImpl();
    }

    @Override
    public User login(String userCode, String password) {
        Connection connection = null;
        User user = null;
        try {
            //通过业务层调用对应的具体数据库操作
            connection = BaseDao.getConnection();
            user = userDao.getLoginUser(connection, userCode, password);
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            BaseDao.closeResource(connection, null, null);
        }
        return user;
    }

    @Override
    public boolean updatePwd(int id, String password) {
        Connection connection = null;
        boolean flag = false;
        try {
            connection = BaseDao.getConnection();
            if (userDao.updatePwd(connection, password, id) > 0) {
                flag = true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            BaseDao.closeResource(connection, null, null);
        }
        return flag;
    }

    @Override
    public int getUserCount(String userName, int userRole) {
        Connection connection = null;
        int count = 0;
        try {
            connection = BaseDao.getConnection();
            count = userDao.queryTheNumberOfUsers(connection, userName, userRole);

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            BaseDao.closeResource(connection, null, null);
        }
        return count;
    }

    @Override
    public List<User> getUserList(String userName, int userRole, int currentPageNo, int pageSize) {
        Connection connection = null;
        List<User> userList = null;
        try {
            connection = BaseDao.getConnection();
            userList = userDao.getUserList(connection, userName, userRole, currentPageNo, pageSize);

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            BaseDao.closeResource(connection, null, null);
        }
        return userList;
    }

    @Override
    public boolean addUser(User user) {
        boolean flag = false;
        Connection conn = null;
        try {
            conn = BaseDao.getConnection();
            //开启MySQL事务
            conn.setAutoCommit(false);
            int updateRows = userDao.addUser(conn,user);
            //提交
            conn.commit();
            if( updateRows > 0 ){
                flag = true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            //回滚
            try {
                conn.rollback();
            } catch (SQLException ex) {
                e.printStackTrace();
            }
        }finally {
            BaseDao.closeResource(conn,null,null);
        }
        return flag;
    }

    @Override
    public boolean delUser(int id) {
        boolean flag = false;
        Connection conn = null;
        try {
            conn = BaseDao.getConnection();
            //开启MySQL事务
            conn.setAutoCommit(false);
            int updateRows = userDao.delUser(conn,id);
            //提交
            conn.commit();
            if( updateRows > 0 ){
                flag = true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            //回滚
            try {
                conn.rollback();
            } catch (SQLException ex) {
                e.printStackTrace();
            }
        }finally {
            BaseDao.closeResource(conn,null,null);
        }
        return flag;
    }

    @Override
    public User viewUser(int id) {
        Connection conn = null;
        User user = null;
        try {
            conn = BaseDao.getConnection();
            user = userDao.viewUser(conn,id);
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            BaseDao.closeResource(conn, null, null);
        }
        return user;
    }

    @Override
    public boolean modify(int id, User user) {
        boolean flag = false;
        Connection conn = null;
        try {
            conn = BaseDao.getConnection();
            //开启MySQL事务
            conn.setAutoCommit(false);
            int updateRows = userDao.modify(conn,id,user);
            //提交
            conn.commit();
            if( updateRows > 0 ){
                flag = true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            //回滚
            try {
                conn.rollback();
            } catch (SQLException ex) {
                e.printStackTrace();
            }
        }finally {
            BaseDao.closeResource(conn,null,null);
        }
        return flag;
    }

    @Test
    public void loginTest() throws SQLException {

        UserServiceImpl service = new UserServiceImpl();
        User admin = service.login("admin", "12345678");
        System.out.println(admin.getUserPassword());
    }

    @Test
    public void userCountTest() throws SQLException {
        UserServiceImpl service = new UserServiceImpl();
        int count = service.getUserCount(null, 0);
        System.out.println(count);
    }

    @Test
    public void getUserListTest() throws SQLException {
        UserServiceImpl service = new UserServiceImpl();
        List<User> userList = service.getUserList(null, 0, 1, 5);
        for (User user : userList) {
            System.out.print(user.getId() + "\t");
            System.out.print(user.getUserName() + "\t");
            System.out.print(user.getUserCode() + "\t");
            System.out.println();
        }
    }
}