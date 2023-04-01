package com.driver.services.impl;

import com.driver.model.*;
import com.driver.repository.ConnectionRepository;
import com.driver.repository.ServiceProviderRepository;
import com.driver.repository.UserRepository;
import com.driver.services.ConnectionService;
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

        if(user.getConnected())throw new Exception("Already connected");//check the connection status


        if(String.valueOf(user.getCountry().getCountryName()).equals(countryName))return user;
        //check if the country of user is the requested country

        //check if user have any service provider
        if(user.getServiceProviderList().isEmpty())throw new Exception("Unable to connect");

        //check if given country can be provided by the service provider
        List<ServiceProvider>  serviceProviderList = user.getServiceProviderList();
       ServiceProvider desiredOne = null;
       Country country = null;

       int id =  Integer.MAX_VALUE;
       for(ServiceProvider s : serviceProviderList){
           for(Country c : s.getCountries()){
               if(String.valueOf(c.getCountryName()).equals(countryName) && s.getId()<id){
                   id = s.getId();
                   desiredOne = s;
                   country = c;
               }
           }
       }
       if(desiredOne==null)throw new Exception("Unable to connect");//no service provider with given country name

        //create the connection
        Connection connection = new Connection();
        connection.setUser(user);
        connection.setServiceProvider(desiredOne);

        String mask = "";
        String code = country.getCode();

        mask = code+"."+id;

        //update user
       user.setMaskedIp(mask);
       user.setConnected(true);

       user.getConnectionList().add(connection);

       desiredOne.getConnectionList().add(connection);


       //save the service provider and all will be saved
        serviceProviderRepository2.save(desiredOne);

        return user;

    }
    @Override
    public User disconnect(int userId) throws Exception {
       User user =userRepository2.findById(userId).get();

       if(user.getConnected()==false)throw new Exception("Already disconnected");

       user.setMaskedIp(null);
       user.setConnected(false);

       userRepository2.save(user);

       return user;
    }
    @Override
    public User communicate(int senderId, int receiverId) throws Exception {
       User sender = userRepository2.findById(senderId).get();

       User receiver =userRepository2.findById(receiverId).get();


       if(receiver.getConnected()==true){//receiver is connected to vpn
           String mask = receiver.getMaskedIp().substring(0,3);

           if(sender.getCountry().getCode().equals(mask)){//receiver is connected to sender country
               return sender;
           }
           //user is in different country so we have to establish a connection first with givencountry name
           String countryName="";
           if(mask.equals(CountryName.AUS.toCode()))countryName= String.valueOf(CountryName.AUS);
           if(mask.equals(CountryName.IND.toCode()))countryName= String.valueOf(CountryName.IND);
           if(mask.equals(CountryName.USA.toCode()))countryName= String.valueOf(CountryName.USA);
           if(mask.equals(CountryName.CHI.toCode()))countryName= String.valueOf(CountryName.CHI);
           if(mask.equals(CountryName.JPN.toCode()))countryName= String.valueOf(CountryName.JPN);

           User updatedSender = connect(senderId,countryName);

           if(!updatedSender.getConnected()){//we are not able to connect the sender with given country
               throw  new Exception("Cannot establish communication");
           }
           return updatedSender;
       }
       else {//if receiver is not connected to vpn

           if(sender.getCountry().equals(receiver.getCountry())){//both are in same country
               return sender;
           }
           //connect the sender with receiver country
           String countryName = receiver.getCountry().getCountryName().toString();
           User updatedSender = connect(senderId,countryName);
           if(!updatedSender.getConnected()){//we are not able to connect the sender with given country
               throw  new Exception("Cannot establish communication");
           }
           return updatedSender;

       }
    }
}
