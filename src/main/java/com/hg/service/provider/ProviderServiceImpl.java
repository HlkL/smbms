package com.hg.service.provider;

import com.hg.dao.BaseDao;
import com.hg.dao.provider.ProviderDao;
import com.hg.dao.provider.ProviderDaoImpl;
import com.hg.pojo.Provider;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author hougen
 * @program smbms
 * @description 供应商业务层
 * @create 2022-10-01 14:43
 */
public class ProviderServiceImpl implements ProviderService{

    ProviderDao providerDao;

    public ProviderServiceImpl() {
        providerDao = new ProviderDaoImpl();
    }

    @Override
    public int getProviderCount(String proCode, String proName) {
        Connection connection = null;
        int count = 0;
        try {
            connection = BaseDao.getConnection();
            count = providerDao.getProviderCount(connection, proCode, proName);
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            BaseDao.closeResource(connection, null, null);
        }
        return count;
    }

    @Override
    public List<Provider> getProviderList(String proCode, String proName, int currentPageNo, int pageSize) {
        List<Provider> providerList = new ArrayList<>();
        Connection connection = null;
        try{
            connection = BaseDao.getConnection();
            providerList = providerDao.getProviderList(connection,proCode,proName,currentPageNo,pageSize);
        } catch (SQLException e) {
            e.printStackTrace();
        }finally {
            BaseDao.closeResource(connection, null, null);
        }
        return providerList;
    }

    @Override
    public boolean addProvider(Provider provider) {
        Connection conn = null;
        boolean flag = false;

        try{
            conn = BaseDao.getConnection();
            conn.setAutoCommit(false);
            int i = providerDao.addProvider(conn, provider);
            if( i > 0 ){
                flag = true;
                conn.commit();
            }
        } catch (SQLException e) {
            e.printStackTrace();
            try {
                conn.rollback();
            } catch (SQLException ex) {
                e.printStackTrace();
            }
        }finally{
            BaseDao.closeResource(conn,null,null);
        }
        return flag;
    }

    @Override
    public boolean delProvider(int proId) {
        Connection conn = null;
        boolean flag = false;

        try{
            conn = BaseDao.getConnection();
            conn.setAutoCommit(false);
            int i = providerDao.delProvider(conn,proId);
            if( i > 0 ){
                flag = true;
                conn.commit();
            }
        } catch (SQLException e) {
            e.printStackTrace();
            try {
                conn.rollback();
            } catch (SQLException ex) {
                e.printStackTrace();
            }
        }finally{
            BaseDao.closeResource(conn,null,null);
        }

        return flag;
    }

    @Override
    public boolean updateProvider(int proId, Provider provider){
        Connection conn = null;
        boolean flag = false;

        try{
            conn = BaseDao.getConnection();
            conn.setAutoCommit(false);
            int i = providerDao.updateProvider(conn,proId,provider);
            if( i > 0 ){
                flag = true;
                conn.commit();
            }
        } catch (SQLException e) {
            e.printStackTrace();
            try {
                conn.rollback();
            } catch (SQLException ex) {
                e.printStackTrace();
            }
        }finally{
            BaseDao.closeResource(conn,null,null);
        }

        return flag;
    }

    @Override
    public Provider viewProvider(int proId){
        Provider provider = null;
        Connection conn = null;

        try{
            conn = BaseDao.getConnection();
            provider = providerDao.viewProvider(conn, proId);
        } catch (SQLException e) {
            e.printStackTrace();
        }finally{
            BaseDao.closeResource(conn,null,null);
        }

        return provider;
    }

    @Override
    public boolean queryProvider(String proCode){
        boolean ifExist = false;
        Connection conn = null;
        try{
            conn = BaseDao.getConnection();
            ifExist = providerDao.queryProvider(conn,proCode);
        } catch (SQLException e) {
            e.printStackTrace();
        }finally{
            BaseDao.closeResource(conn,null,null);
        }
        return ifExist;
    }
}


