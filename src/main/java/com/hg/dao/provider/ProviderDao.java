package com.hg.dao.provider;

import com.hg.pojo.Provider;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

/**
 * 订单数据库操作
 *
 * @author HG
 */
public interface ProviderDao {

    /**
     *  通过编码或者名称查询供应商列表
     * @param conn 数据库连接对象
     * @param proCode 编码
     * @param proName 名称
     * @param currentPageNo 当前页码
     * @param pageSize 页面大小
     * @return 供应商列表
     * @throws SQLException
     */
    List<Provider> getProviderList(Connection conn, String proCode,String proName,int currentPageNo, int pageSize )throws SQLException;

    /**
     * 获取供应商信息个数
     *
     * @param conn 数据库连接对象
     * @param proCode 编码
     * @param proName 名称
     * @return 个数
     * @throws SQLException
     */
    int getProviderCount(Connection conn,String proCode,String proName)throws SQLException;

    /**
     *  添加供应商信息
     *
     * @param conn
     * @param provider
     * @return
     * @throws SQLException
     */
    int addProvider(Connection conn,Provider provider)throws SQLException;

    /**
     *  删除供应商
     *
     * @param conn
     * @param proId
     * @return
     * @throws SQLException
     */
    int delProvider(Connection conn,int proId)throws SQLException;

    /**
     *  修改供应商信息
     *
     * @param conn
     * @param proId
     * @param provider
     * @return
     * @throws SQLException
     */
    int updateProvider(Connection conn,int proId,Provider provider)throws SQLException;

    /**
     *  查看供应商信息
     *
     * @param conn
     * @param proId
     * @return
     * @throws SQLException
     */
    Provider viewProvider(Connection conn,int proId)throws SQLException;

    /**
     *  查询供应商是否存在
     *
     * @param conn
     * @param proCode
     * @return
     * @throws SQLException
     */
    boolean queryProvider(Connection conn,String proCode)throws SQLException;
}
