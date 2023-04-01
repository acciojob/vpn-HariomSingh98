package com.driver.services.impl;

import com.driver.model.*;
import com.driver.repository.ConnectionRepository;
import com.driver.repository.ServiceProviderRepository;
import com.driver.repository.UserRepository;
import com.driver.services.ConnectionService;
import io.swagger.models.auth.In;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
public class ConnectionServiceImpl implements ConnectionService {
    @Autowired
    UserRepository userRepository2;
    @Autowired
    ServiceProviderRepository serviceProviderRepository2;
    @Autowired
    ConnectionRepository connectionRepository2;


    @Override
   public User connect(int userId, String countryName) throws Exception{
       User user = userRepository2.findById(userId).get();

       if(user.getMaskedIp()!=null){
           throw new Exception("Already connected");
       }//check the connection status


       if(user.getOriginalCountry().getCountryName().toString().equalsIgnoreCase(countryName))return user;
       //check if the country of user is the requested country

        //check if user have any service provider
       if(user.getServiceProviderList()==null)throw new Exception("Unable to connect");

       //check if given country can be provided by the service provider
        List<ServiceProvider>  serviceProviderList = user.getServiceProviderList();
        ServiceProvider desiredOne = null;
        Country country = null;

       int id =  Integer.MAX_VALUE;

       for(ServiceProvider s : serviceProviderList){
           for(Country c : s.getCountryList()){
               if(c.getCountryName().toString().equalsIgnoreCase(countryName) && s.getId()<id){
                   id = s.getId();
                   desiredOne = s;
                   country = c;
               }
           }
       }

        //create the connection
        Connection connection = new Connection();
        connection.setUser(user);
        connection.setServiceProvider(desiredOne);

        String mask = "";
        String code = country.getCode();

        mask = code+"."+desiredOne.getId()+"."+user.getId();

        //update user
       user.setMaskedIp(mask);
       user.setConnected(true);

       user.getConnectionList().add(connection);

       desiredOne.getConnectionList().add(connection);


       //save the service provider and all will be saved

        userRepository2.save(user);
        serviceProviderRepository2.save(desiredOne);



        return user;

    }
    @Override
    public User disconnect(int userId) throws Exception {
       User user =userRepository2.findById(userId).get();

       if(!user.getConnected())throw new Exception("Already disconnected");

       user.setMaskedIp(null);
       user.setConnected(false);

       userRepository2.save(user);

       return user;
    }



    @Override
    public User communicate(int senderId, int receiverId) throws Exception {
        User user = userRepository2.findById(senderId).get();
        User user1 = userRepository2.findById(receiverId).get();

        if(user1.getMaskedIp()!=null){
            String str = user1.getMaskedIp();
            String cc = str.substring(0,3); //chopping country code = cc

            if(cc.equals(user.getOriginalCountry().getCode()))
                return user;
            else {
                String countryName = "";

                if (cc.equalsIgnoreCase(CountryName.IND.toCode()))
                    countryName = CountryName.IND.toString();
                if (cc.equalsIgnoreCase(CountryName.USA.toCode()))
                    countryName = CountryName.USA.toString();
                if (cc.equalsIgnoreCase(CountryName.JPN.toCode()))
                    countryName = CountryName.JPN.toString();
                if (cc.equalsIgnoreCase(CountryName.CHI.toCode()))
                    countryName = CountryName.CHI.toString();
                if (cc.equalsIgnoreCase(CountryName.AUS.toCode()))
                    countryName = CountryName.AUS.toString();

                User user2 = connect(senderId,countryName);
                if (!user2.getConnected()){
                    throw new Exception("Cannot establish communication");

                }
                else return user2;
            }

        }
        else{
            if(user1.getOriginalCountry().equals(user.getOriginalCountry())){
                return user;
            }
            String countryName = user1.getOriginalCountry().getCountryName().toString();
            User user2 =  connect(senderId,countryName);
            if (!user2.getConnected()){
                throw new Exception("Cannot establish communication");
            }
            else return user2;

        }
    }
}
