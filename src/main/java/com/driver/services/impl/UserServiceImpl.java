package com.driver.services.impl;

import com.driver.model.*;
import com.driver.repository.CountryRepository;
import com.driver.repository.ServiceProviderRepository;
import com.driver.repository.UserRepository;
import com.driver.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    UserRepository userRepository3;
    @Autowired
    ServiceProviderRepository serviceProviderRepository3;
    @Autowired
    CountryRepository countryRepository3;

    @Override
    public User register(String username, String password, String countryName) throws Exception{

         countryName = countryName.toUpperCase();

         CountryName cn= null;



         for(CountryName c : CountryName.values()){//iterate over enum constants

             if(c.name().equals(countryName)){
                 cn = c;
                 break;
             }
         }
         if(cn==null)throw new Exception("Country not found");


        // create country object set country attribute
        Country country = new Country();
        country.setCountryName(cn);
        country.setCode(cn.toCode());


        //create new object
        User user= new User();
        user.setConnected(false);
        user.setUsername(username);
        user.setPassword(password);
        String code = country.getCode()+"."+user.getId();
        user.setOriginalIp(code);
        user.setMaskedIp(null);
        user.setOriginalCountry(country);


        country.setUser(user);

        userRepository3.save(user);

        return user;
    }

    @Override
    public User subscribe(Integer userId, Integer serviceProviderId) {
        ServiceProvider serviceProvider = serviceProviderRepository3.findById(serviceProviderId).get();

        User user = userRepository3.findById(userId).get();



        //update user
        user.getServiceProviderList().add(serviceProvider);


        //update service provider
        serviceProvider.getUsers().add(user);



        //save the parent service provider
        serviceProviderRepository3.save(serviceProvider);


        return user;
    }
}
