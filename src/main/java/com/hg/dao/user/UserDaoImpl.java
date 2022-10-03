package com.hg.dao.user;

import com.hg.dao.BaseDao;
import com.hg.pojo.User;
import com.mysql.cj.util.StringUtils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * @program: smbms
 * @description: 实现userDao接口 dao层抛出异常，让service层去捕获处理
 * @author: hougen
 * @create: 2022-09-23 13:11
 */
public class UserDaoImpl implements UserDao {
    @Override
    public User getLoginUser(Connection conn, String userCode, String password) {
        PreparedStatement pstm = null;
        ResultSet rs = null;
        User user = null;
        if (conn != null) {
            String sql = "SELECT * FROM smbms_user WHERE userCode=?";
            Object[] params = {userCode};
            try {
                //得到数据库所有对象
                rs = BaseDao.execute(conn, pstm, rs, sql, params);
                if (rs.next()) {
                    user = new User();
                    user.setId(rs.getInt("id"));
                    user.setUserCode(rs.getString("userCode"));
                    user.setUserName(rs.getString("userName"));
                    user.setUserPassword(rs.getString("userPassword"));
                    user.setGender(rs.getInt("gender"));
                    user.setBirthday(rs.getDate("birthday"));
                    user.setPhone(rs.getString("phone"));
                    user.setAddress(rs.getString("address"));
                    user.setUserRole(rs.getInt("userRole"));
                    user.setCreatedBy(rs.getInt("createdBy"));
                    user.setCreationDate(rs.getTimestamp("creationDate"));
                    user.setModifyBy(rs.getInt("modifyBy"));
                    user.setModifyDate(rs.getTimestamp("modifyDate"));
                }
                //连接层可能出现业务,conn关流在事务中做
                BaseDao.closeResource(null, pstm, rs);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return user;
    }

    @Override
    public int updatePwd(Connection conn, String password, int id) throws SQLException {
        PreparedStatement pstm = null;
        int execute = 0;
        if (conn != null) {
            String sql = "UPDATE smbms_user SET userPassword=? WHERE id=?";
            Object[] params = {password, id};
            execute = BaseDao.executeUpdate(conn, pstm, sql, params);
            BaseDao.closeResource(null, pstm, null);
        }
        return execute;
    }

    @Override
    public int queryTheNumberOfUsers(Connection conn, String userName, int userRole) throws SQLException {
        PreparedStatement pstm = null;
        ResultSet rs = null;
        int count = 0;
        if (conn != null) {
            //用于拼接sql
            StringBuffer sql = new StringBuffer();
            //联表查询sql
            sql.append("SELECT COUNT(1) AS count FROM smbms_user u, smbms_role r WHERE u.userRole = r.id");

            //存放sql参数
            ArrayList<Object> list = new ArrayList<Object>();
            //读取搜索框将要查询的用户名
            if (!StringUtils.isNullOrEmpty(userName)) {
                sql.append(" AND u.userName LIKE ?");
                list.add("%" + userName + "%");
            }
            if (userRole > 0) {
                //用户角色选择查询
                //AND前面有空格
                sql.append(" AND u.userRole LIKE ?");
                list.add("%" + userRole + "%");
            }
            //把list转化为数组
            Object[] params = list.toArray();
            ResultSet resultSet = BaseDao.execute(conn, pstm, rs, sql.toString(), params);
            if (resultSet.next()) {
                //从结果集获取最总数量
                count = resultSet.getInt("count");
            }
            BaseDao.closeResource(null, pstm, resultSet);
        }
        return count;
    }

    @Override
    public List<User> getUserList(Connection conn, String userName, int userRole, int currentPageNo, int pageSize) throws SQLException {
        PreparedStatement pstm = null;
        ResultSet rs = null;
        List<User> userList = new ArrayList<User>();
        if (conn != null) {
            StringBuffer sql = new StringBuffer();
            sql.append("SELECT u.*,r.roleName AS userRoleName FROM smbms_user u,smbms_role r WHERE u.userRole = r.id");
            List<Object> list = new ArrayList<Object>();
            if (!StringUtils.isNullOrEmpty(userName)) {
                sql.append(" AND u.userName LIKE ?");
                list.add("%" + userName + "%");
            }
            if (userRole > 0) {
                sql.append(" AND u.userRole LIKE ?");
                list.add(userRole);
            }
            //在mysql数据库中，分页使用 limit startIndex，pageSize ; 总数
            /*
                LIMIT出现在查询语句的最后，可以使用一个参数或两个参数来限制取出的数据。
                其中第一个参数代表偏移量：offset（可选参数），第二个参数代表取出的数据条数：rows。
                当指定一个参数时，默认省略了偏移量，即偏移量为0，从第一行数据开始取，一共取rows条。
                当指定两个参数时 i 和 j 时，需要注意偏移量的取值是从0开始的，从第i行数据开始取，一共取 j 条

                获得数据总条数
                SELECT COUNT(*) FROM Student;
                假设每页显示10条，则直接进行除法运算，然后向上取整
                SELECT CEIL(COUNT(*) / 10) AS pageTotal FROM Student;

                第1页：第1~10条，SQL写法：LIMIT 0,10
                第2页：第11~20条，SQL写法：LIMIT 10,10
                第3页：第21~30条，SQL写法：LIMIT 20,10

                据此我们可以总结出，LIMIT所需要的两个参数计算公式如下：
                offset： (pageNumber - 1) * pageSize
                rows：   pageSize
                                                                                                 */

            sql.append(" ORDER BY creationDate DESC LIMIT ?,?");
            currentPageNo = (currentPageNo - 1) * pageSize;
            list.add(currentPageNo);
            list.add(pageSize);

            Object[] params = list.toArray();
            rs = BaseDao.execute(conn, pstm, rs, sql.toString(), params);
            while (rs.next()) {
                User user = new User();
                user.setId(rs.getInt("id"));
                user.setUserCode(rs.getString("userCode"));
                user.setUserName(rs.getString("userName"));
                user.setGender(rs.getInt("gender"));
                user.setBirthday(rs.getDate("birthday"));
                user.setPhone(rs.getString("phone"));
                user.setUserRole(rs.getInt("userRole"));
                user.setUserRoleName(rs.getString("userRoleName"));
                userList.add(user);
            }
            BaseDao.closeResource(null, pstm, rs);
        }
        return userList;
    }

    @Override
    public int addUser(Connection conn, User user) throws SQLException {
        PreparedStatement pstm = null;
        int updateRows = 0;
        if (null != conn) {
            String sql = "insert into smbms_user (userCode,userName,userPassword," +
                    "userRole,gender,birthday,phone,address,creationDate,createdBy) " +
                    "values(?,?,?,?,?,?,?,?,?,?)";
            Object[] params = {user.getUserCode(), user.getUserName(), user.getUserPassword(),
                    user.getUserRole(), user.getGender(), user.getBirthday(),
                    user.getPhone(), user.getAddress(), user.getCreationDate(), user.getCreatedBy()};
            updateRows = BaseDao.executeUpdate(conn, pstm, sql, params);
            BaseDao.closeResource(null, pstm, null);
        }
        return updateRows;
    }

    @Override
    public int delUser(Connection conn, int id) throws SQLException {
        PreparedStatement pstm = null;
        int updateRows = 0;
        if( conn != null ) {
            String sql = "DELETE FROM smbms_user WHERE id=?";
            Object[] params ={id};
            updateRows = BaseDao.executeUpdate(conn, pstm, sql, params);
            BaseDao.closeResource(null, pstm, null);
        }
        return updateRows;
    }

    @Override
    public User viewUser(Connection conn, int id) throws SQLException {
        PreparedStatement pstm = null;
        ResultSet rs = null;
        User user = null;
        if( conn != null ) {
            String sql = "SELECT u.*,r.roleName AS userRoleName FROM smbms_user u,smbms_role r WHERE u.id = ? AND u.userRole = r.id";

            Object[] params ={id};
            rs = BaseDao.execute(conn,pstm,rs,sql,params);
            if( rs.next() ){
                user = new User();
                user.setId(rs.getInt("id"));
                user.setUserCode(rs.getString("userCode"));
                user.setUserName(rs.getString("userName"));
                user.setUserPassword(rs.getString("userPassword"));
                user.setGender(rs.getInt("gender"));
                user.setBirthday(rs.getDate("birthday"));
                user.setPhone(rs.getString("phone"));
                user.setAddress(rs.getString("address"));
                user.setUserRole(rs.getInt("userRole"));
                user.setCreatedBy(rs.getInt("createdBy"));
                user.setCreateDate(rs.getTimestamp("creationDate"));
                user.setModifyBy(rs.getInt("modifyBy"));
                user.setModifyDate(rs.getTimestamp("modifyDate"));
                user.setUserRoleName(rs.getString("userRoleName"));
            }
            BaseDao.closeResource(null, pstm, null);
        }
        return user;
    }

    @Override
    public int modify(Connection conn, int id, User user) throws SQLException {
        PreparedStatement pstm = null;
        int updateRows = 0;
        if( conn != null ) {
            String sql = "UPDATE smbms_user SET userName = ?,gender = ?,birthday =?,phone = ?,address = ?,userRole = ?,modifyBy = ?,modifyDate = ? WHERE id = ?";
            Object[] params = {user.getUserName(),user.getGender(),user.getBirthday(),user.getPhone(),user.getAddress(),user.getUserRole(),user.getModifyBy(),user.getModifyDate(),id};
            updateRows = BaseDao.executeUpdate(conn,pstm,sql,params);
            BaseDao.closeResource(null,pstm,null);
        }
        return updateRows;
    }
}