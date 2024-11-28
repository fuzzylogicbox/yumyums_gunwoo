package com.yum.yumyums.service.seller;

import com.yum.yumyums.dto.seller.SellerDTO;
import com.yum.yumyums.entity.seller.Seller;
import com.yum.yumyums.repository.seller.SellerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class SellerServiceImpl implements SellerService {
    private final SellerRepository sellerRepository;
    private final RestTemplate restTemplate = new RestTemplate();

    @Value("${api.key.business}")
    private String apiKey;


    @Override
    public void save(SellerDTO sellerDTO) {
        Seller seller = Seller.toSaveEntity(sellerDTO);
        sellerRepository.save(seller);
    }

    @Override
    public SellerDTO findById(String id) {
        Optional<Seller> optionalSeller = sellerRepository.findById(id);
        if(optionalSeller.isPresent()){
            Seller seller = optionalSeller.get();
            SellerDTO sellerDTO = SellerDTO.toSellerDTO(seller);
            return sellerDTO;
        }else{
            return null;
        }
    }

    @Override
    public String verifyBusiness(String masterName, String sellerNum) {
        String url = String.format("https://bizno.net/api/fapi?key=%s&gb=1&status=Y&ceo=%s&q=%s&type=json",
                apiKey, masterName, sellerNum);
        return restTemplate.getForObject(url, String.class);
    }

}
