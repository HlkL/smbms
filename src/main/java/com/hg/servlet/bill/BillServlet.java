package com.hg.servlet.bill;

import com.google.gson.Gson;
import com.hg.pojo.Bill;
import com.hg.pojo.Provider;
import com.hg.pojo.User;
import com.hg.service.bill.BillService;
import com.hg.service.bill.BillServiceImpl;
import com.hg.service.provider.ProviderService;
import com.hg.service.provider.ProviderServiceImpl;
import com.hg.utils.Constants;
import com.hg.utils.PageSupport;
import com.mysql.cj.util.StringUtils;

import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.annotation.*;
import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.*;

/**
 * 订单管理
 *
 * @author HG
 */
@WebServlet(name = "BillServlet", value = "/BillServlet")
public class BillServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String method = request.getParameter("method");
        System.out.println("订单管理页面----->"+method);

        if( "query".equals(method) ){
            this.getBillList(request, response);
        }else if( "bcexist".equals(method) ){
            this.isExistBillCode(request, response);
        }else if( "getproviderlist".equals(method) ){
            this.getProviderList(request, response);
        }else if( "add".equals(method) ){
            this.addBill(request, response);
        } else if ("view".equals(method)) {
            this.viewBill(request, response,"billview.jsp");
        }else if ("modify".equals(method)) {
            this.viewBill(request, response,"billmodify.jsp");
        }else if ("modifysave".equals(method)) {
            this.updateBill(request, response);
        }else if ("delbill".equals(method)) {
            try {
                this.delBill( request, response);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doGet(request, response);
    }

    /**
     *  订单列表显示
     *
     * @param request
     * @param response
     * @throws ServletException
     * @throws IOException
     */
    private void getBillList(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        ProviderService providerService = new ProviderServiceImpl();
        //获取供应商数量
        int providerCount = providerService.getProviderCount(null, null);
        //获取供应商列表
        List<Provider> providerList = providerService.getProviderList(null, null, 1, providerCount);
        request.setAttribute("providerList",providerList);

        String productName = request.getParameter("queryProductName");
        String providerIdTemp = request.getParameter("queryProviderId");
        String isPaymentTemp = request.getParameter("queryIsPayment");
        String pageIndex = request.getParameter("pageIndex");
        int providerId = 0;
        int isPayment = 0;
        int currentPageNo = 1;

        if( StringUtils.isNullOrEmpty(productName) ){
            productName = "";
        }
        if( !StringUtils.isNullOrEmpty(providerIdTemp) ){
            providerId = Integer.parseInt(providerIdTemp);
        }
        if( !StringUtils.isNullOrEmpty(isPaymentTemp) ){
            isPayment = Integer.parseInt(isPaymentTemp);
        }
        if( !StringUtils.isNullOrEmpty(pageIndex) ){
            currentPageNo = Integer.parseInt(pageIndex);
        }


        BillService billService = new BillServiceImpl();
        int billCount = billService.getBillCount(productName, providerId, isPayment);

        //分页支持
        PageSupport pageSupport = new PageSupport();
        pageSupport.setPageSize(Constants.PAGESIZE);
        pageSupport.setTotalCount(billCount);
        pageSupport.setCurrentPageNo(currentPageNo);
        //设置分页
        int totalPageCount = pageSupport.getTotalPageCount();
        currentPageNo = pageSupport.getCurrentPageNo();
        int totalCount = pageSupport.getTotalCount();
        //设置首尾页
        if( currentPageNo < 1 ){
            currentPageNo = 1;
        }
        if( currentPageNo > totalPageCount ){
            currentPageNo = totalPageCount;
        }

        List<Bill> billList = billService.getBillList(productName, providerId, isPayment, currentPageNo, Constants.PAGESIZE);
        request.setAttribute("billList",billList);

        request.setAttribute("totalCount",totalCount);
        request.setAttribute("currentPageNo",currentPageNo);
        request.setAttribute("totalPageCount",totalPageCount);

        request.getRequestDispatcher("billlist.jsp").forward(request,response);
    }

    /**
     *  检查订单编号是否存在
     */
    private void isExistBillCode(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String billCode = request.getParameter("billCode");
        Map<String, String> resultMap = new HashMap<>();
        if( StringUtils.isNullOrEmpty(billCode) ){
            resultMap.put("billCode","empty");
        }else {
            BillService billService = new BillServiceImpl();
            if( billService.ifExistBillCode(billCode) ){
                resultMap.put("billCode","exist");
            }else{
                resultMap.put("billCode","notexist");
            }
        }

        Gson gson = new Gson();
        response.setContentType("application/json");
        PrintWriter writer = response.getWriter();
        writer.write(gson.toJson(resultMap));
        writer.flush();
        writer.close();
    }

    /**
     *  获取供应商列表
     */
    private void getProviderList(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        ProviderService providerService = new ProviderServiceImpl();
        //获取供应商数量
        int providerCount = providerService.getProviderCount(null, null);
        //获取供应商列表
        List<Provider> providerList = providerService.getProviderList(null, null, 1, providerCount);
        //把providerList转换成json对象返回
        resp.setContentType("application/json");
        PrintWriter writer = resp.getWriter();
        writer.write(new Gson().toJson(providerList));
        writer.flush();
        writer.close();
    }

    /**
     *  添加订单信息
     *
     * @param request
     * @param response
     */
    private void addBill(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        String billCode = request.getParameter("billCode");
        String productName = request.getParameter("productName");
        String productDesc = request.getParameter("productDesc");
        String productUnit = request.getParameter("productUnit");

        String productCount = request.getParameter("productCount");
        String totalPrice = request.getParameter("totalPrice");
        String providerId = request.getParameter("providerId");
        String isPayment = request.getParameter("isPayment");

        //创建一个Bill对象，并且根据表单获取的内容设置其属性值
        Bill bill = new Bill();
        bill.setBillCode(billCode);
        bill.setProductName(productName);
        bill.setProductDesc(productDesc);
        bill.setProductUnit(productUnit);
        bill.setProductCount(new BigDecimal(productCount).setScale(2, BigDecimal.ROUND_DOWN));
        bill.setIsPayment(Integer.parseInt(isPayment));
        bill.setTotalPrice(new BigDecimal(totalPrice).setScale(2, BigDecimal.ROUND_DOWN));
        bill.setProviderId(Integer.parseInt(providerId));
        bill.setCreatedBy(((User) request.getSession().getAttribute(Constants.USER_SESSION)).getId());
        bill.setCreationDate(new Date());

        boolean flag = false;
        BillService billService = new BillServiceImpl();
        try {
            flag = billService.addBill(bill);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (flag) {
            response.sendRedirect(request.getContextPath() + "/jsp/bill.do?method=query");
        } else {
            request.getRequestDispatcher("billadd.jsp").forward(request, response);
        }
    }

    /**
     *  查看订单信息
     */
    private void viewBill(HttpServletRequest request, HttpServletResponse response,String url) throws ServletException, IOException {
        String billid = request.getParameter("billid");
        if (!StringUtils.isNullOrEmpty(billid)) {
            BillServiceImpl billService = new BillServiceImpl();
            Bill biller = billService.viewBill(Integer.parseInt(billid));
            request.setAttribute("bill", biller);
            request.getRequestDispatcher(url).forward(request, response);
        }
    }

    /**
     *  修改订单信息
     */
    private void updateBill(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException{
        String id = request.getParameter("id");
        String productName = request.getParameter("productName");
        String productDesc = request.getParameter("productDesc");
        String productUnit = request.getParameter("productUnit");
        String productCount = request.getParameter("productCount");
        String totalPrice = request.getParameter("totalPrice");
        String providerId = request.getParameter("providerId");
        String isPayment = request.getParameter("isPayment");

        Bill bill = new Bill();
        bill.setId(Integer.valueOf(id));
        bill.setProductName(productName);
        bill.setProductDesc(productDesc);
        bill.setProductUnit(productUnit);
        bill.setProductCount(new BigDecimal(productCount).setScale(2, BigDecimal.ROUND_DOWN));
        bill.setIsPayment(Integer.parseInt(isPayment));
        bill.setTotalPrice(new BigDecimal(totalPrice).setScale(2, BigDecimal.ROUND_DOWN));
        bill.setProviderId(Integer.parseInt(providerId));

        bill.setModifyBy(((User) request.getSession().getAttribute(Constants.USER_SESSION)).getId());
        bill.setModifyDate(new Date());
        boolean flag = false;
        BillService billService = new BillServiceImpl();
        try {
            flag = billService.updateBill(Integer.parseInt(id),bill);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (flag) {
            response.sendRedirect(request.getContextPath() + "/jsp/bill.do?method=query");
        } else {
            request.getRequestDispatcher("billmodify.jsp").forward(request, response);
        }
    }

    /**
     *  删除订单
     */
    private void delBill(HttpServletRequest request, HttpServletResponse response) throws IOException, SQLException {
        String id = request.getParameter("billid");
        int delId = 0;
        Map<String, String> map = new HashMap<String, String>();
        try {
            delId = Integer.parseInt(id);
        } catch (Exception e) {
            e.printStackTrace();
        }
        BillService billService = new BillServiceImpl();
        //通过id判断订单是否存在
        Bill bill = billService.viewBill(delId);
        if( bill == null ){
            map.put("delResult", "notexist");
        }else{
            if( bill.getIsPayment() == 2 ){
                if( billService.delBill(delId) ){
                    map.put("delResult", "true");
                }else{
                    map.put("delResult", "false");
                }
            } else if ( bill.getIsPayment() == 1 ) {
                map.put("delResult", "cannot");
            }
        }

        response.setContentType("application/json");
        PrintWriter writer = response.getWriter();
        writer.write(new Gson().toJson(map));
        writer.flush();
        writer.close();
    }
}