package com.hg.service.bill;

import com.hg.pojo.Bill;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

/**
 * 订单管理业务层
 * @author HG
 */
public interface BillService {
    /**
     *  获取订单数量
     */
    int getBillCount(String productName, int providerId, int isPayment);

    /**
     *  获取订单信息
     */
    List<Bill> getBillList(String productName, int providerId, int isPayment, int currentPageNo, int pageSize);



    boolean addBill(Bill bill);

    boolean delBill(int id) throws SQLException;

    Bill viewBill(int id);

    boolean updateBill(int id,Bill bill);

//    int ifPayment(int id) throws SQLException;

    boolean ifExistBillCode(String billCode);


}
