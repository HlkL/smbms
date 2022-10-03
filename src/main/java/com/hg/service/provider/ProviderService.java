package com.hg.service.provider;

import com.hg.pojo.Provider;

import java.util.List;

/**
 * @author HG
 */
public interface ProviderService {

    /**
     * 获取供应商信息个数
     *
     * @param proCode 编码
     * @param proName 名称
     * @return 个数
     */
    int getProviderCount(String proCode,String proName);

    /**
     *  获取每页供应商信息
     *
     * @param proCode 编号
     * @param proName 名称
     * @param currentPageNo 当前页码
     * @param pageSize 页面大小
     * @return 供应商信息
     */
    List<Provider> getProviderList(String proCode, String proName, int currentPageNo, int pageSize);

    /**
     * 添加供应商信息
     *
     * @param provider 供应商
     * @return 添加是否成功
     */
    boolean addProvider(Provider provider);

    /**
     * 删除供应商信息
     *
     * @param proId id
     * @return 删除否成功
     */
    boolean delProvider(int proId);

    /**
     *  修改供应商信息
     *
     * @param proId
     * @param provider
     * @return
     */
    boolean updateProvider(int proId, Provider provider);

    /**
     *  查看供应商信息
     *
     * @param proId
     * @return
     */
    Provider viewProvider(int proId);

    /**
     *  查询供应商是否存在
     *
     * @param proCode
     * @return
     */
    boolean queryProvider(String proCode);
}
