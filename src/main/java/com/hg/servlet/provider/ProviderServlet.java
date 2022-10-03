package com.hg.servlet.provider;

import com.google.gson.Gson;
import com.hg.dao.BaseDao;
import com.hg.pojo.Provider;
import com.hg.pojo.User;
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
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 供应商管理
 *
 * @author HG
 */
@WebServlet(name = "ProviderServlet", value = "/ProviderServlet")
public class ProviderServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String method = request.getParameter("method");
        System.out.println("method--------->" + method);

        if ("query".equals(method)) {
            this.queryProvider(request, response);
        } else if ("add".equals(method)) {
            this.addProvider(request, response);
        } else if ("pcexist".equals(method)) {
            this.ifExistCode(request, response);
        } else if ("view".equals(method)) {
            this.viewProvider(request, response, "providerview.jsp");
        } else if ("modify".equals(method)) {
            this.viewProvider(request, response, "providermodify.jsp");
        } else if ("modifyexe".equals(method)) {
            this.updateProvider(request, response);
        } else if ("delprovider".equals(method)) {
            this.delProvider(request, response);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doGet(request, response);
    }

    /**
     * 供应商管理
     */
    private void queryProvider(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String queryProCode = request.getParameter("queryProCode");
        String queryProName = request.getParameter("queryProName");
        String pageIndex = request.getParameter("pageIndex");

        int currentPageNo = 1;

        if (queryProCode == null) {
            queryProCode = "";
        }
        if (queryProName == null) {
            queryProName = "";
        }
        if (pageIndex != null) {
            currentPageNo = Integer.parseInt(pageIndex);
        }

        ProviderService providerService = new ProviderServiceImpl();
        //获取供应商所有数据
        int proCount = providerService.getProviderCount(queryProCode, queryProName);

        //分页
        PageSupport pageSupport = new PageSupport();
        pageSupport.setPageSize(Constants.PAGESIZE);
        pageSupport.setTotalCount(proCount);
        pageSupport.setCurrentPageNo(currentPageNo);

        currentPageNo = pageSupport.getCurrentPageNo();
        int totalPageCount = pageSupport.getTotalPageCount();
        int totalCount = pageSupport.getTotalCount();

        //首页和尾页设置
        if (currentPageNo < 1) {
            currentPageNo = 1;
        }
        if (currentPageNo > totalPageCount) {
            currentPageNo = totalPageCount;
        }

        List<Provider> providerList = providerService.getProviderList(queryProCode, queryProName, currentPageNo, Constants.PAGESIZE);

        request.setAttribute("providerList", providerList);
        request.setAttribute("totalCount", totalCount);
        request.setAttribute("currentPageNo", currentPageNo);
        request.setAttribute("totalPageCount", totalPageCount);
        request.setAttribute("queryProCode", queryProCode);
        request.setAttribute("queryProName", queryProName);

        request.getRequestDispatcher("providerlist.jsp").forward(request, response);
    }

    /**
     * 添加供应商
     */
    private void addProvider(HttpServletRequest request, HttpServletResponse response) {
        String proCode = request.getParameter("proCode");
        String proName = request.getParameter("proName");
        String proContact = request.getParameter("proContact");
        String proPhone = request.getParameter("proPhone");
        String proAddress = request.getParameter("proAddress");
        String proFax = request.getParameter("proFax");
        String proDesc = request.getParameter("proDesc");

        Provider provider = new Provider();

        provider.setProCode(proCode);
        provider.setProName(proName);
        provider.setProDesc(proDesc);
        provider.setProContact(proContact);
        provider.setProPhone(proPhone);
        provider.setProAddress(proAddress);
        provider.setProFax(proFax);
        provider.setCreatedBy(((User) request.getSession().getAttribute(Constants.USER_SESSION)).getId());
        provider.setCreationDate(new Date());

        ProviderService providerService = new ProviderServiceImpl();

        if (providerService.addProvider(provider)) {
            try {
                response.sendRedirect(request.getContextPath() + "/jsp/provider.do?method=query");
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            try {
                request.getRequestDispatcher("provideradd.jsp").forward(request, response);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 验证供应商编号是否存在
     */
    private void ifExistCode(HttpServletRequest request, HttpServletResponse response) {
        ProviderService providerService = new ProviderServiceImpl();

        String proCode = request.getParameter("proCode");
        Map<String, String> resultMap = new HashMap<>();

        if (null == proCode || "".equals(proCode)) {
            resultMap.put("proCode", "empty");
        } else {
            if (providerService.queryProvider(proCode)) {
                resultMap.put("proCode", "exist");
            } else {
                resultMap.put("proCode", "notExist");
            }
        }

        try {
            Gson gson = new Gson();
            response.setContentType("application/json");
            PrintWriter writer = response.getWriter();
            writer.write(gson.toJson(resultMap));

            writer.flush();
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 查看某个供应商的具体信息
     */
    private void viewProvider(HttpServletRequest request, HttpServletResponse response, String url) throws ServletException, IOException {
        String proid = request.getParameter("proid");
        int proId = Integer.parseInt(proid);

        ProviderService providerService = new ProviderServiceImpl();
        Provider provider = providerService.viewProvider(proId);

        request.setAttribute("provider", provider);
        request.getRequestDispatcher(url).forward(request, response);
    }

    /**
     * 修改供应商信息
     */
    private void updateProvider(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String proid = request.getParameter("providerid");
        String proCode = request.getParameter("proCode");
        String proName = request.getParameter("proName");
        String proContact = request.getParameter("proContact");
        String proPhone = request.getParameter("proPhone");
        String proAddress = request.getParameter("proAddress");
        String proFax = request.getParameter("proFax");
        String proDesc = request.getParameter("proDesc");

        Provider provider = new Provider();

        provider.setProCode(proCode);
        provider.setProName(proName);
        provider.setProDesc(proDesc);
        provider.setProContact(proContact);
        provider.setProPhone(proPhone);
        provider.setProAddress(proAddress);
        provider.setProFax(proFax);
        provider.setModifyBy(((User) request.getSession().getAttribute(Constants.USER_SESSION)).getId());
        provider.setModifyDate(new Date());

        ProviderService providerService = new ProviderServiceImpl();
        if ( providerService.updateProvider(Integer.parseInt(proid), provider) ){
            response.sendRedirect(request.getContextPath()+"/jsp/provider.do?method=query");
        }else{
            request.getRequestDispatcher("providermodify.jsp").forward(request,response);
        }
    }

    /**
     * 删除供应商信息
     */
    private void delProvider(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String proid = request.getParameter("proid");
        ProviderService providerService = new ProviderServiceImpl();
        Map<String, String> resultMap = new HashMap<>();
        if( !StringUtils.isNullOrEmpty(proid) ) {
            if ( providerService.delProvider(Integer.parseInt(proid)) ) {
                resultMap.put("delResult","true");
            }else{
                resultMap.put("delResult","false");
            }
        }else {
            resultMap.put("delResult","notexist");
        }
        Gson gson = new Gson();
        response.setContentType("application/json");
        PrintWriter writer = response.getWriter();
        writer.write(gson.toJson(resultMap));
        writer.flush();
        writer.close();
    }
}
