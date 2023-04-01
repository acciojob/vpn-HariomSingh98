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
        Country country = new Country();
        try {
             String name = String.valueOf(CountryName.valueOf(countryName));
         }
        catch(Exception e){
            throw new Exception("Invalid Country");
        }
        //set country attribute
        String code = CountryName.valueOf(countryName).toCode();
        country.setCode(code);
        country.setCountryName(CountryName.valueOf(countryName));

        //create new object
        User user= new User();
        user.setConnected(false);
        user.setUsername(username);
        user.setPassword(password);
        user.setMaskedIp(null);
        user.setOriginalIp(code+"."+user.getId());


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
