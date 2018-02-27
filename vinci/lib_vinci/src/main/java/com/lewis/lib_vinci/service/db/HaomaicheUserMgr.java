package com.lewis.lib_vinci.service.db;

import com.lewis.lib_vinci.utils.LogUtil;

import java.util.List;

/**
 * <p>
 * 类作用说明: 用户信息管理类
 * <p>
 * 类的参数说明:
 *
 * @date 2015/05/22
 */
public class HaomaicheUserMgr {
    private static final String TAG = HaomaicheUserMgr.class.getSimpleName();
    private static HaomaicheUserMgr mOnlineUserMgr;
    private static AccountModel mCurrentAccountModel;

    public static HaomaicheUserMgr getInit() {
        if (mOnlineUserMgr == null)
            mOnlineUserMgr = new HaomaicheUserMgr();
        return mOnlineUserMgr;
    }

    public static int getCurrentAccountId(String telNum) {

        int accountid = -1;
        try {
            String sql = "select id from " + AccountModel.TABLE_NAME
                    + " where empName=?";
            String[] parms = new String[]{telNum};
            AccountModel tempAccountModel = (AccountModel) HaomaicheDbMgr
                    .getInstance().getInfobySql(sql, parms, AccountModel.class);
            if (tempAccountModel != null)
                accountid = tempAccountModel.getId();
        } catch (Throwable e) {
            LogUtil.e(TAG, "AidAccountMgr==getCurrentAccountId", e);
        }

        return accountid;
    }

    // 得到本地的所有账号
    public static List<AccountModel> getLocalAccountList() {
        try {
            String sql = "select * from " + AccountModel.TABLE_NAME
                    + " order by logintime desc";
            List list = HaomaicheDbMgr.getInstance().getInfosbySql(sql, null,
                    AccountModel.class);
            return list;
        } catch (Throwable e) {
            LogUtil.e(TAG, "AccountMgr==getLocalAccountList", e);
        }
        return null;
    }

    // 更新账户登录时间
    public static void updateAccountLoginTime(int id, long currentTime) {
        try {
            String update_sql1 = "update " + AccountModel.TABLE_NAME
                    + " set logintime=? where id=?";
            Object[] object = new Object[]{currentTime, id};
            HaomaicheDbMgr.getInstance().updateInfo(update_sql1, object);
        } catch (Throwable e) {
            LogUtil.e(TAG, "AccountMgr==updateAccountLoginTime", e);
        }
    }

    public static void saveAccountModel(AccountModel mAccountModel) {
        try {
            mAccountModel.setLogintime(System.currentTimeMillis());
            int accountid = getCurrentAccountId(mAccountModel.getEmpName());
            HaomaicheDbMgr.getInstance().saveInfos(AccountModel.TABLE_NAME, mAccountModel, "id", accountid);
            mAccountModel.setId(getCurrentAccountId(mAccountModel.getEmpName()));
            if (mCurrentAccountModel == null)
                mCurrentAccountModel = mAccountModel;
        } catch (Throwable e) {
            LogUtil.e(TAG, "AccountMgr==saveAccountModel", e);
        }
    }

    public static AccountModel getAccountModel() {
        if (mCurrentAccountModel != null) {
            return mCurrentAccountModel;
        }
        return null;
    }


    /**
     * 更新服务范围
     *
     * @param id
     * @param areaValue
     */
    public static void updateServiceArea(int id, String areaValue) {
        try {
            String update_sql1 = "update " + AccountModel.TABLE_NAME
                    + " set areaValue=? where id=?";
            Object[] object = new Object[]{areaValue, id};
            HaomaicheDbMgr.getInstance().updateInfo(update_sql1, object);
        } catch (Throwable e) {
            LogUtil.e(TAG, "AccountMgr==updateServiceArea", e);
        }
    }
}
