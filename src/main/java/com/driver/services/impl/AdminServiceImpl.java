package com.driver.services.impl;

import com.driver.model.Admin;
import com.driver.model.Country;
import com.driver.model.CountryName;
import com.driver.model.ServiceProvider;
import com.driver.repository.AdminRepository;
import com.driver.repository.CountryRepository;
import com.driver.repository.ServiceProviderRepository;
import com.driver.services.AdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AdminServiceImpl implements AdminService {
    @Autowired
    AdminRepository adminRepository1;

    @Autowired
    ServiceProviderRepository serviceProviderRepository1;

    @Autowired
    CountryRepository countryRepository1;

    @Override
    public Admin register(String username, String password) {
        //create a new Admin object
        Admin newAdmin =  new Admin();
        newAdmin.setUsername(username);
        newAdmin.setPassword(password);

        adminRepository1.save(newAdmin);

        return newAdmin;
    }

    @Override
    public Admin addServiceProvider(int adminId, String providerName) {
        //create a service provider object
        ServiceProvider serviceProvider = new ServiceProvider();
        serviceProvider.setName(providerName);

        Admin admin = adminRepository1.findById(adminId).get();
        serviceProvider.setAdmin(admin);

        admin.getServiceProviders().add(serviceProvider);


        adminRepository1.save(admin);

        return admin;
    }

    @Override
    public ServiceProvider addCountry(int serviceProviderId, String countryName) throws Exception{
        ServiceProvider  serviceProvider = serviceProviderRepository1.findById(serviceProviderId).get();

        countryName = countryName.toUpperCase();


        if(countryName.equals("IND") || countryName.equals("AUS") ||countryName.equals("USA") ||countryName.equals("CHI") || countryName.equals("JPN")){
            //create a country object
            Country country = new Country();
            CountryName c  = CountryName.valueOf(countryName);

            country.setCountryName(c);
            String code = c.toCode();
            country.setCode(code);
            //user will be null

            country.setServiceProvider(serviceProvider);

            serviceProvider.getCountryList().add(country);


            serviceProviderRepository1.save(serviceProvider);

            return serviceProvider;
        }

        throw new Exception("Country not found");

    }
}
