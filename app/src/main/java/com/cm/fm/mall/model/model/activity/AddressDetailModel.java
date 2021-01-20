package com.cm.fm.mall.model.model.activity;

import com.cm.fm.mall.contract.activity.AddressDetailContract;
import com.cm.fm.mall.model.bean.AddressInfo;
import com.cm.fm.mall.common.util.LogUtil;

import org.litepal.crud.DataSupport;

import java.util.List;

public class AddressDetailModel implements AddressDetailContract.Model {
    private String tag = "TAG_AddressDetailModel";

    @Override
    public void saveOrUpdateAddressInfoM(AddressInfo curAddressInfo,String name,
            String phone,String address,String street,boolean isDefaultAddress,String selectedTagText) {
        int type = curAddressInfo==null ? 0 : 1;
        AddressInfo info;
        if(type == 0){
            /** 点击 新建地址 进来，直接保存 */
            info = new AddressInfo();
        }else {
            /** 点击 编辑按钮 进来，需要修改 */
            info = DataSupport.find(AddressInfo.class,curAddressInfo.getId());
        }
        info.setUsername(name);
        info.setPhone(phone);
        info.setAddress(address);
        info.setStreet(street);
        info.setDefault(isDefaultAddress);
        info.setTag(selectedTagText);
        LogUtil.d(tag,info.toString());
        //不管是新加还是修改，先直接保存
        info.save();
        updateDefaultAddress(type,info,isDefaultAddress);
    }

    /**
     * 重置所有地址的默认状态为false
     * @param type 1 修改  0 新加
     * @param info 当前收货人信息
     */
    private void updateDefaultAddress(int type,AddressInfo info,boolean isDefaultAddress){
        //修改信息时没有将当前收货人的地址设置为默认地址，则不重置其他收货人的默认地址记录
        if(type == 1  && !isDefaultAddress){
            return;
        }
        //遍历所有的地址信息，将非当前收货人 都设置为 非默认地址，并保存
        List<AddressInfo> addressInfos = DataSupport.findAll(AddressInfo.class);
        for (AddressInfo ai:addressInfos) {
            if(info.getId() != ai.getId()){
                ai.setDefault(false);
                ai.save();
            }
        }

    }
}
