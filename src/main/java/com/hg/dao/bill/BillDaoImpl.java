package com.hg.dao.bill;

import com.hg.dao.BaseDao;
import com.hg.pojo.Bill;
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
 * @description 订单管理
 * @create 2022-10-02 14:58
 */
public class BillDaoImpl implements BillDao{
    @Override
    public int getBillCount(Connection conn, String productName, int providerId, int isPayment) throws SQLException {
        int count = 0;
        PreparedStatement pasm = null;
        ResultSet rs = null;
        if( conn != null ){
            conn = BaseDao.getConnection();
            StringBuffer sql = new StringBuffer();
            sql.append("SELECT count(1) count FROM smbms_bill b,smbms_provider p WHERE b.providerId=p.id");
            List<Object> list = new ArrayList<>();
            if( !StringUtils.isNullOrEmpty(productName) ){
                sql.append(" AND productName LIKE ?");
                list.add("%" + productName + "%");
            }
            if( providerId > 0 ){
                sql.append(" AND providerId = ?");
                list.add(providerId);
            }
            if( isPayment > 0 ){
                sql.append(" AND isPayment = ?");
                list.add(isPayment);
            }

            Object[] params = list.toArray();
            rs = BaseDao.execute(conn,pasm,rs,sql.toString(),params);
            if ( rs.next() ){
                count = rs.getInt("count");
            }

            BaseDao.closeResource(null,pasm,rs);
        }
        return count;
    }

    @Override
    public List<Bill> getBillList(Connection conn, String productName, int providerId, int isPayment, int currentPageNo, int pageSize) throws SQLException {
        List<Bill> bills = new ArrayList<Bill>();
        PreparedStatement pasm = null;
        ResultSet rs = null;
        if( conn != null ){
            StringBuffer sql = new StringBuffer();
            sql.append("select b.*,p.proName as proName from smbms_bill b,smbms_provider p where b.providerId = p.id ");
            List<Object> list = new ArrayList<>();
            if( !StringUtils.isNullOrEmpty(productName) ){
                sql.append(" AND productName LIKE ?");
                list.add("%" + productName + "%");
            }
            if( providerId > 0 ){
                sql.append(" AND providerId = ?");
                list.add(providerId);
            }
            if( isPayment >0 ){
                sql.append(" AND isPayment = ?");
                list.add(isPayment);
            }
            sql.append(" ORDER BY creationDate DESC LIMIT ?,?");
            currentPageNo = ( currentPageNo - 1 )*pageSize;
            list.add(currentPageNo);
            list.add(pageSize);

            Object[] params = list.toArray();
            rs = BaseDao.execute(conn,pasm,rs,sql.toString(),params);
            while( rs.next() ){
                Bill bill = new Bill();
                bill.setId(rs.getInt("id"));
                bill.setBillCode(rs.getString("billCode"));
                bill.setProductName(rs.getString("productName"));
                bill.setProductDesc(rs.getString("productDesc"));
                bill.setProductUnit(rs.getString("productUnit"));
                bill.setProductCount(rs.getBigDecimal("productCount"));
                bill.setTotalPrice(rs.getBigDecimal("totalPrice"));
                bill.setIsPayment(rs.getInt("isPayment"));
                bill.setCreationDate(rs.getTimestamp("creationDate"));
                bill.setCreatedBy(rs.getInt("createdBy"));
                bill.setProviderId(rs.getInt("providerId"));
                bill.setProviderName(rs.getString("proName"));
                bills.add(bill);
            }
        }
        return bills;
    }



    @Override
    public int addBill(Connection conn,Bill bill) throws SQLException{
        PreparedStatement pstm = null;
        int addRows = 0;
        if (null != conn) {
            String sql = "insert into smbms_bill (billCode,productName,productDesc," +
                    "productUnit,productCount,totalPrice,isPayment,providerId,createdBy,creationDate) " +
                    "values(?,?,?,?,?,?,?,?,?,?)";
            Object[] params = {bill.getBillCode(), bill.getProductName(), bill.getProductDesc(),
                    bill.getProductUnit(), bill.getProductCount(), bill.getTotalPrice(), bill.getIsPayment(),
                    bill.getProviderId(), bill.getCreatedBy(), bill.getCreationDate()};
            addRows = BaseDao.executeUpdate(conn, pstm, sql, params);
            BaseDao.closeResource(null, pstm, null);
        }
        return addRows;
    }

    @Override
    public int delBill(Connection conn,int id) throws SQLException{
        PreparedStatement pstm = null;
        int flag = 0;
        if (null != conn) {
            String sql = "delete from smbms_bill where id=?";
            Object[] params = {id};
            flag = BaseDao.executeUpdate(conn, pstm, sql, params);
            BaseDao.closeResource(null, pstm, null);
        }
        return flag;
    }

    @Override
    public Bill viewBill(Connection conn,int id) throws SQLException{
        PreparedStatement pstm = null;
        ResultSet rs = null;
        Bill bill = null;
        if (conn != null) {
            String sql = "select b.*,p.proName as proName from smbms_bill b,smbms_provider p where b.providerId = p.id and b.id=?";
            Object[] params = {id};
            rs = BaseDao.execute(conn, pstm, rs, sql, params);
            while (rs.next()) {
                bill = new Bill();
                bill.setId(rs.getInt("id"));
                bill.setBillCode(rs.getString("billCode"));
                bill.setProductName(rs.getString("productName"));
                bill.setProductDesc(rs.getString("productDesc"));
                bill.setProductUnit(rs.getString("productUnit"));
                bill.setProductCount(rs.getBigDecimal("productCount"));
                bill.setTotalPrice(rs.getBigDecimal("totalPrice"));
                bill.setIsPayment(rs.getInt("isPayment"));
                bill.setCreationDate(rs.getTimestamp("creationDate"));
                bill.setCreatedBy(rs.getInt("createdBy"));
                bill.setProviderId(rs.getInt("providerId"));
                bill.setProviderName(rs.getString("proName"));
            }
            BaseDao.closeResource(null, pstm, null);
        }
        return bill;
    }

    @Override
    public int updateBill(Connection conn,int id,Bill bill) throws SQLException{
        PreparedStatement pstm = null;
        int updateRows = 0;
        if (null != conn) {
            String sql = "update smbms_bill set productName=?," +
                    "productDesc=?,productUnit=?,productCount=?,totalPrice=?," +
                    "isPayment=?,providerId=?,modifyBy=?,modifyDate=? where id = ? ";
            Object[] params = {bill.getProductName(), bill.getProductDesc(),
                    bill.getProductUnit(), bill.getProductCount(), bill.getTotalPrice(), bill.getIsPayment(),
                    bill.getProviderId(), bill.getModifyBy(), bill.getModifyDate(), id};
            updateRows = BaseDao.executeUpdate(conn, pstm, sql, params);
            BaseDao.closeResource(null, pstm, null);
        }
        return updateRows;
    }

    @Override
    public int ifPayment(Connection conn, int id) throws SQLException{
        PreparedStatement pstm = null;
        ResultSet rs = null;
        int payment = 0;
        if (conn != null) {
            String sql = "SELECT smbms_bill.isPayment as isPayment from smbms_bill where id = ?";
            Object[] params = {id};
            rs = BaseDao.execute(conn, pstm, rs, sql, params);
            if( rs.next() ){
                payment = rs.getInt("isPayment");
            }
            BaseDao.closeResource(null, pstm, null);
        }
        return payment;
    }

    @Override
    public Bill ifExistBillCode(Connection connection, String billCode) throws Exception {
        PreparedStatement pstm = null;
        ResultSet rs = null;
        Bill bill = null;
        if (connection != null) {
            String sql = "select * from smbms_bill where billCode = ? ";
            Object[] params = {billCode};
            rs = BaseDao.execute(connection, pstm, rs, sql, params);
            while (rs.next()) {
                bill = new Bill();
                bill.setId(rs.getInt("id"));
                bill.setBillCode(rs.getString("billCode"));
                bill.setProductName(rs.getString("productName"));
                bill.setProductDesc(rs.getString("productDesc"));
                bill.setProductUnit(rs.getString("productUnit"));
                bill.setProductCount(rs.getBigDecimal("productCount"));
                bill.setTotalPrice(rs.getBigDecimal("totalPrice"));
                bill.setIsPayment(rs.getInt("isPayment"));
                bill.setCreationDate(rs.getTimestamp("creationDate"));
                bill.setCreatedBy(rs.getInt("createdBy"));
                bill.setProviderId(rs.getInt("providerId"));
            }
            BaseDao.closeResource(null, pstm, null);
        }
        return bill;
    }


}


