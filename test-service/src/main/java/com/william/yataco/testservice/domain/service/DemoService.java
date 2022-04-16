package com.william.yataco.testservice.domain.service;

import com.william.yataco.testservice.application.data.MovementResponse;
import com.william.yataco.testservice.application.data.UserRequest;
import com.william.yataco.testservice.domain.model.Movement;
import com.william.yataco.testservice.domain.model.User;
import com.william.yataco.testservice.domain.model.UserToken;
import com.william.yataco.testservice.domain.port.api.DemoServicePort;
import com.william.yataco.testservice.domain.port.spi.DemoIntegrationPort;
import com.william.yataco.testservice.domain.port.spi.DemoPersistencePort;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class DemoService implements DemoServicePort {

    private final DemoIntegrationPort demoIntegrationPort;
    private final DemoPersistencePort demoPersistencePort;

    public DemoService(DemoIntegrationPort demoIntegrationPort, DemoPersistencePort demoPersistencePort) {
        this.demoIntegrationPort = demoIntegrationPort;
        this.demoPersistencePort = demoPersistencePort;
    }

    @Override
    public List<MovementResponse> processEvent(UserRequest userRequest) {
        //Login
        UserToken userToken = demoIntegrationPort.loginToUser(userRequest);
        //Me
        User user = demoIntegrationPort.getInfoUser(userToken.getAccessToken());
        //Save first 10 movements
        this.saveMovements(userToken.getAccessToken(),user.getId(),0);
        //Save next 10 movements
        this.saveMovements(userToken.getAccessToken(),user.getId(),10);
        //Get Movements from Database
        List<Movement> movementList = demoPersistencePort.getMovements();
        return movementList.stream().map(movement -> MovementResponse.builder()
                        .id(movement.getId())
                        .amount(movement.getAmount())
                        .dateCreated(movement.getDateCreated())
                        .description(movement.getDescription())
                        .type(movement.getType())
                        .build())
                .collect(Collectors.toList());
    }

    private void saveMovements(String accessToken,String identifier, int offSet){
        //Movements
        List<Movement> movements = demoIntegrationPort.getMovements(accessToken,identifier,offSet);
        //Save Movements
        demoPersistencePort.saveMovements(movements);
    }
}
