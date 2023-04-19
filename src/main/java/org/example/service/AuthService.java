package org.example.service;


import com.example.grpc.AuthServiceGrpc.AuthServiceImplBase;
import com.example.grpc.LoginRequest;
import com.example.grpc.LoginResponse;
import com.example.grpc.SignupRequest;
import com.example.grpc.SignupResponse;
import io.grpc.stub.StreamObserver;
import org.example.entity.User;
import org.example.repository.UserRepository;
import org.lognet.springboot.grpc.GRpcService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Optional;

@GRpcService
public class AuthService extends AuthServiceImplBase {
    @Autowired
    private UserRepository userRepository;

    @Override
    public void signup(SignupRequest request, StreamObserver<SignupResponse> responseObserver) {
        Optional<User> u = userRepository.findByEmail("test1@gmail.com");
        if (!u.isEmpty()) {
            User user = new User();
            user.setEmail(request.getEmail());
            user.setPassword(request.getPassword());
            User newUser = userRepository.save(user);
            SignupResponse response = SignupResponse.newBuilder().setSuccess(true).build();
            responseObserver.onNext(response);
        } else {
//            responseObserver.onError("Email already exists");
        }
        responseObserver.onCompleted();
    }

    @Override
    public void login(LoginRequest request, StreamObserver<LoginResponse> responseObserver) {
        Optional<User> u = userRepository.findByEmail("test1@gmail.com");
        if (!u.isEmpty()) {
            System.out.println(u.get().getId());
        }
        super.login(request, responseObserver);
    }
}