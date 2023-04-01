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


//    @Override
//    public User connect(int userId, String countryName) throws Exception{
//        User user = userRepository2.findById(userId).get();
//
//        if(user.getMaskedIp()!=null){
//            throw new Exception("Already connected");
//        }//check the connection status
//
//
//        if(user.getOriginalCountry().getCountryName().toString().equalsIgnoreCase(countryName))return user;
//        //check if the country of user is the requested country
//
//        //check if user have any service provider
//        if(user.getServiceProviderList().isEmpty())throw new Exception("Unable to connect");
//
//        //check if given country can be provided by the service provider
//        List<ServiceProvider>  serviceProviderList = user.getServiceProviderList();
//        ServiceProvider desiredOne = null;
//        Country country = null;
//
//       int id =  Integer.MAX_VALUE;
//
//       for(ServiceProvider s : serviceProviderList){
//           for(Country c : s.getCountryList()){
//               if(c.getCountryName().toString().equalsIgnoreCase(countryName) && s.getId()<id){
//                   id = s.getId();
//                   desiredOne = s;
//                   country = c;
//               }
//           }
//       }
//       if(desiredOne==null)throw new Exception("Unable to connect");//no service provider with given country name
//
//        //create the connection
//        Connection connection = new Connection();
//        connection.setUser(user);
//        connection.setServiceProvider(desiredOne);
//
//        String mask = "";
//        String code = country.getCode();
//
//        mask = code+"."+desiredOne.getId()+"."+user.getId();
//
//        //update user
//       user.setMaskedIp(mask);
//       user.setConnected(true);
//
//       user.getConnectionList().add(connection);
//
//       desiredOne.getConnectionList().add(connection);
//
//
//       //save the service provider and all will be saved
//        serviceProviderRepository2.save(desiredOne);
//
//        return user;
//
//    }
//    @Override
//    public User disconnect(int userId) throws Exception {
//       User user =userRepository2.findById(userId).get();
//
//       if(!user.getConnected())throw new Exception("Already disconnected");
//
//       user.setMaskedIp(null);
//       user.setConnected(false);
//
//       userRepository2.save(user);
//
//       return user;
//    }
//    @Override
//    public User communicate(int senderId, int receiverId) throws Exception {
//       User sender = userRepository2.findById(senderId).get();
//
//       User receiver =userRepository2.findById(receiverId).get();
//
//
//       if(receiver.getConnected()){//receiver is connected to vpn
//           String mask = receiver.getMaskedIp().substring(0,3);
//
//           if(sender.getOriginalCountry().getCode().equals(mask)){//receiver is connected to sender country
//               return sender;
//           }
//           //user is in different country so we have to establish a connection first with given country name
//           String countryName="";
//           if(mask.equals(CountryName.AUS.toCode()))countryName= CountryName.AUS.toString();
//           if(mask.equals(CountryName.IND.toCode()))countryName= CountryName.IND.toString();
//           if(mask.equals(CountryName.USA.toCode()))countryName= CountryName.USA.toString();
//           if(mask.equals(CountryName.CHI.toCode()))countryName= CountryName.CHI.toString();
//           if(mask.equals(CountryName.JPN.toCode()))countryName= CountryName.JPN.toString();
//
//           User updatedSender = connect(senderId,countryName);
//
//           if(!updatedSender.getConnected()){//we are not able to connect the sender with given country
//               throw  new Exception("Cannot establish communication");
//           }
//           return updatedSender;
//       }
//       else {//if receiver is not connected to vpn
//
//           if(sender.getOriginalCountry().equals(receiver.getOriginalCountry())){//both are in same country
//               return sender;
//           }
//           //connect the sender with receiver country
//           String countryName = receiver.getOriginalCountry().getCountryName().toString();
//           User updatedSender = connect(senderId,countryName);
//           if(!updatedSender.getConnected()){//we are not able to connect the sender with given country
//               throw  new Exception("Cannot establish communication");
//           }
//           return updatedSender;
//
//       }
//}


    @Override
    public User connect(int userId, String countryName) throws Exception{

        User user = userRepository2.findById(userId).get();
        if(user.getMaskedIp()!=null){
            throw new Exception("Already connected");
        }
        else if(countryName.equalsIgnoreCase(user.getOriginalCountry().getCountryName().toString())){
            return user;
        }
        else {
            if (user.getServiceProviderList()==null){
                throw new Exception("Unable to connect");
            }

            List<ServiceProvider> serviceProviderList = user.getServiceProviderList();
            int a = Integer.MAX_VALUE;
            ServiceProvider serviceProvider = null;
            Country country =null;

            for(ServiceProvider serviceProvider1:serviceProviderList){

                List<Country> countryList = serviceProvider1.getCountryList();

                for (Country country1: countryList){

                    if(countryName.equalsIgnoreCase(country1.getCountryName().toString()) && a > serviceProvider1.getId() ){
                        a=serviceProvider1.getId();
                        serviceProvider=serviceProvider1;
                        country=country1;
                    }
                }
            }
            if (serviceProvider!=null){
                Connection connection = new Connection();
                connection.setUser(user);
                connection.setServiceProvider(serviceProvider);

                String cc = country.getCode();
                int givenId = serviceProvider.getId();
                String mask = cc+"."+givenId+"."+userId;

                user.setMaskedIp(mask);
                user.setConnected(true);
                user.getConnectionList().add(connection);

                serviceProvider.getConnectionList().add(connection);

                userRepository2.save(user);

                serviceProviderRepository2.save(serviceProvider);


            }
        }
        return user;
    }
    @Override
    public User disconnect(int userId) throws Exception {
        User user = userRepository2.findById(userId).get();
        if(user.getConnected()==false){
            throw new Exception("Already disconnected");
        }
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
