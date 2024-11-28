package com.yum.yumyums.service.seller;

import com.yum.yumyums.dto.seller.SellerDTO;

public interface SellerService {
    void save(SellerDTO sellerDTO);
    SellerDTO findById(String id);
    String verifyBusiness(String masterName, String sellerNum);
}
