package com.hg.dao.provider;

import com.hg.dao.BaseDao;
import com.hg.pojo.Provider;
import com.hg.pojo.User;
import com.mysql.cj.util.StringUtils;
import org.junit.Test;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author hougen
 * @program smbms
 * @description 订单数据库操作
 * @create 2022-09-30 21:57
 */
public class ProviderDaoImpl implements ProviderDao{

    @Override
    public List<Provider> getProviderList(Connection conn, String proCode, String proName,int currentPageNo, int pageSize) throws SQLException {
        PreparedStatement pstm = null;
        ResultSet rs = null;
        List<Provider> providerList = new ArrayList<>();
        List<Object> list = new ArrayList<>();
        if( conn != null ){
            StringBuffer sql = new StringBuffer();
            sql.append("SELECT * FROM smbms_provider WHERE TRUE");
            if (!StringUtils.isNullOrEmpty(proCode)) {
                sql.append(" AND proCode LIKE ?");
                list.add("%" + proCode + "%");
            }
            if (!StringUtils.isNullOrEmpty(proName)) {
                sql.append(" AND proName LIKE ?");
                list.add("%" + proName + "%");
            }
            //分页
            sql.append(" ORDER BY creationDate DESC LIMIT ?,?");
            currentPageNo = (currentPageNo -1)*pageSize;
            list.add(currentPageNo);
            list.add(pageSize);

            Object[] params = list.toArray();
            rs = BaseDao.execute(conn, pstm, rs, sql.toString(), params);
            while (rs.next()) {
                Provider provider = new Provider();
                provider.setId(rs.getInt("id"));
                provider.setProCode(rs.getString("proCode"));
                provider.setProName(rs.getString("proName"));
                provider.setProDesc(rs.getString("proDesc"));
                provider.setProContact(rs.getString("proContact"));
                provider.setProPhone(rs.getString("proPhone"));
                provider.setProAddress(rs.getString("proAddress"));
                provider.setProFax(rs.getString("proFax"));
                provider.setCreationDate(rs.getTimestamp("creationDate"));
                providerList.add(provider);
            }
            BaseDao.closeResource(null,pstm,rs);
        }
        return providerList;
    }

    @Override
    public int getProviderCount(Connection conn,String proCode,String proName) throws SQLException {
        int providerCount = 0;
        PreparedStatement pstm = null;
        ResultSet rs = null;
        List<Object> list = new ArrayList<>();
        if( conn != null ) {
            StringBuffer sql = new StringBuffer();
            sql.append("SELECT COUNT(1) AS count FROM smbms_provider WHERE TRUE");
            if (!StringUtils.isNullOrEmpty(proCode)) {
                sql.append(" AND proCode LIKE ?");
                list.add("%" + proCode + "%");
            }
            if (!StringUtils.isNullOrEmpty(proName)){
                sql.append(" AND proName LIKE ?");
                list.add("%" + proName + "%");
            }
            Object[] params = list.toArray();
            rs = BaseDao.execute(conn, pstm, rs, sql.toString(), params);
            if( rs.next() ){
                providerCount = rs.getInt("count");
            }
            BaseDao.closeResource(null,pstm,rs);
        }
        return providerCount;
    }

    @Override
    public int addProvider(Connection conn, Provider provider) throws SQLException {
        PreparedStatement pstm = null;
        int providerRows = 0;
        if (null != conn) {
            String sql = "insert into smbms_provider (proCode,proName,proDesc," +
                    "proContact,proPhone,proAddress,proFax,createdBy,creationDate) " +
                    "values(?,?,?,?,?,?,?,?,?)";
            Object[] params = {provider.getProCode(), provider.getProName(), provider.getProDesc(),
                    provider.getProContact(), provider.getProPhone(), provider.getProAddress(),
                    provider.getProFax(), provider.getCreatedBy(), provider.getCreationDate()};
            providerRows = BaseDao.executeUpdate(conn, pstm, sql, params);
            BaseDao.closeResource(null, pstm, null);
        }
        return providerRows;
    }

    @Override
    public int delProvider(Connection conn, int proId) throws SQLException {
        PreparedStatement pstm = null;
        int providerRows = 0;
        if (null != conn) {
            String sql = "DELETE FROM smbms_provider WHERE id = ?";
            Object[] params = {proId};
            providerRows = BaseDao.executeUpdate(conn, pstm, sql, params);
            BaseDao.closeResource(null, pstm, null);
        }
        return providerRows;
    }

    @Override
    public int updateProvider(Connection conn, int proId, Provider provider) throws SQLException {
        int providerRows = 0;
        PreparedStatement pstm = null;
        if (null != conn) {
            String sql = "UPDATE smbms_provider SET proName=?,proDesc=?,proContact=?," +
                    "proPhone=?,proAddress=?,proFax=?,modifyBy=?,modifyDate=? WHERE id = ? ";
            Object[] params = {provider.getProName(), provider.getProDesc(), provider.getProContact(), provider.getProPhone(), provider.getProAddress(),
                    provider.getProFax(), provider.getModifyBy(), provider.getModifyDate(),proId};
            providerRows = BaseDao.executeUpdate(conn, pstm, sql, params);
            BaseDao.closeResource(null, pstm, null);
        }
        return providerRows;
    }

    @Override
    public Provider viewProvider(Connection conn, int proId) throws SQLException {
        PreparedStatement pstm = null;
        ResultSet rs = null;
        Provider provider = null;
        if( conn != null ){
            String sql = "SELECT * FROM smbms_provider WHERE id = ?";
            Object[] params = {proId};
            rs = BaseDao.execute(conn, pstm, rs, sql, params);
            if (rs.next()) {
                provider = new Provider();
                provider.setId(rs.getInt("id"));
                provider.setProCode(rs.getString("proCode"));
                provider.setProName(rs.getString("proName"));
                provider.setProDesc(rs.getString("proDesc"));
                provider.setProContact(rs.getString("proContact"));
                provider.setProPhone(rs.getString("proPhone"));
                provider.setProAddress(rs.getString("proAddress"));
                provider.setProFax(rs.getString("proFax"));
                provider.setCreatedBy(rs.getInt("createdBy"));
                provider.setCreationDate(rs.getTimestamp("creationDate"));
                provider.setModifyBy(rs.getInt("modifyBy"));
                provider.setModifyDate(rs.getTimestamp("modifyDate"));
            }
            BaseDao.closeResource(null,pstm,rs);
        }
        return provider;
    }

    @Override
    public boolean queryProvider(Connection conn, String proCode) throws SQLException {
        boolean ifExist = false;
        PreparedStatement pstm = null;
        ResultSet rs = null;
        if( conn != null ){
            String sql = "SELECT * FROM smbms_provider WHERE proCode = ?";
            Object[] params = {proCode};
            rs =  BaseDao.execute(conn,pstm,rs,sql,params);
            if( rs.next() ){
                ifExist = true;
            }
            BaseDao.closeResource(null,pstm,rs);
        }
        return ifExist;
    }

    @Test
    public void test() throws SQLException {
        Connection conn = null;
        conn = BaseDao.getConnection();
        List<Provider> bj = getProviderList(conn, "", null, 1, 5);

        int providerCount = getProviderCount(conn, "B", "");
        for (Provider provider : bj) {
            System.out.println(provider.getProName()+"\t"+provider.getProCode());
        }
        System.out.println("providerCount is -->"+providerCount);

        System.out.println(queryProvider(conn,"BJ_GYS002"));
    }
}


