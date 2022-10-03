package com.hg.dao.bill;


import com.hg.pojo.Bill;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

/**
 *  订单管理数据库操作
 *
 * @author HG
 */
public interface BillDao {
    /**
     *  获取订单数量
     *
     * @param conn 数据库连接
     * @param productName 产品名称
     * @param providerId 供应商id
     * @param isPayment 是否支付
     * @return
     */
    int getBillCount(Connection conn, String productName, int providerId, int isPayment) throws SQLException;

    /**
     *  获取订单
     *
     * @param conn
     * @param productName
     * @param providerId
     * @param isPayment
     * @param currentPageNo
     * @param pageSize
     * @return
     * @throws SQLException
     */
    List<Bill> getBillList(Connection conn, String productName, int providerId, int isPayment, int currentPageNo, int pageSize) throws SQLException;

    int addBill(Connection conn,Bill bill) throws SQLException;

    int delBill(Connection conn,int id) throws SQLException;

    Bill viewBill(Connection conn,int id) throws SQLException;

    int updateBill(Connection conn,int id,Bill bill) throws SQLException;

    int ifPayment(Connection conn,int id) throws SQLException;

    Bill ifExistBillCode(Connection connection, String billCode) throws Exception;
}
