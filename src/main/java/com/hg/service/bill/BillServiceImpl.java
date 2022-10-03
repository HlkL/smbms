package com.hg.service.bill;

import com.hg.dao.BaseDao;
import com.hg.dao.bill.BillDao;
import com.hg.dao.bill.BillDaoImpl;
import com.hg.pojo.Bill;
import com.hg.utils.Constants;
import org.junit.Test;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

/**
 * @author hougen
 * @program smbms
 * @description 订单管理业务层
 * @create 2022-10-02 15:26
 */
public class BillServiceImpl implements BillService{

    private final int isPayment = 2;

    private BillDao billDao;

    public BillServiceImpl() {
        this.billDao = new BillDaoImpl();
    }

    @Override
    public int getBillCount(String productName, int providerId, int isPayment) {
        Connection conn = null;
        int billCount = 0;
        try{
            conn = BaseDao.getConnection();
            billCount = billDao.getBillCount(conn, productName, providerId, isPayment);
        } catch (SQLException e) {
            e.printStackTrace();
        }finally{
            BaseDao.closeResource(conn,null,null);
        }
        return billCount;
    }

    @Override
    public List<Bill> getBillList(String productName, int providerId, int isPayment, int currentPageNo, int pageSize){
        Connection conn = null;
        List<Bill> bills = null;
        try{
            conn = BaseDao.getConnection();
            bills = billDao.getBillList(conn, productName, providerId, isPayment,currentPageNo,pageSize);
        } catch (SQLException e) {
            e.printStackTrace();
        }finally{
            BaseDao.closeResource(conn,null,null);
        }
        return bills;
    }

    @Override
    public boolean addBill(Bill bill){
        Connection conn = null;
        boolean flag = false;
        try{
            conn = BaseDao.getConnection();
            conn.setAutoCommit(false);
            int i = billDao.addBill(conn, bill);
            if( i > 0 ){
                flag = true;
                conn.commit();
            }
        } catch (SQLException e) {
            try {
                conn.rollback();
            } catch (SQLException ex) {
                e.printStackTrace();
            }
            e.printStackTrace();
        }finally{
            BaseDao.closeResource(conn,null,null);
        }
        return flag;
    }

    @Override
    public boolean delBill(int id) throws SQLException {
        Connection conn = BaseDao.getConnection();
        boolean flag = false;
        if( billDao.ifPayment(conn, id) == this.isPayment){
            try{
                conn.setAutoCommit(false);
                int i = billDao.delBill(conn, id);
                if( i > 0 ){
                    flag = true;
                    conn.commit();
                }
            } catch (SQLException e) {
                try {
                    conn.rollback();
                } catch (SQLException ex) {
                    e.printStackTrace();
                }
                e.printStackTrace();
            }finally{
                BaseDao.closeResource(conn,null,null);
            }
        }
        return flag;
    }

    @Override
    public Bill viewBill(int id){
        Connection conn = null;
        Bill bill = null;
        try{
            conn = BaseDao.getConnection();
            bill = billDao.viewBill(conn,id);
        } catch (SQLException e) {
            e.printStackTrace();
        }finally{
            BaseDao.closeResource(conn,null,null);
        }
        return bill;
    }

    @Override
    public boolean updateBill(int id, Bill bill){
        Connection conn = null;
        boolean flag = false;
        try{
            conn = BaseDao.getConnection();
            conn.setAutoCommit(false);
            int i = billDao.updateBill(conn, id,bill);
            if( i > 0 ){
                flag = true;
                conn.commit();
            }
        } catch (SQLException e) {
            try {
                conn.rollback();
            } catch (SQLException ex) {
                e.printStackTrace();
            }
            e.printStackTrace();
        }finally{
            BaseDao.closeResource(conn,null,null);
        }
        return flag;
    }

    @Override
    public boolean ifExistBillCode(String billCode){
        Connection conn = null;
        boolean flag = false;
        try{
            conn = BaseDao.getConnection();
            if( billDao.ifExistBillCode(conn,billCode) != null ){
                flag = true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }finally{
            BaseDao.closeResource(conn,null,null);
        }
        return flag;
    }

}
