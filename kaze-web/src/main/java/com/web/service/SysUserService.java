package com.web.service;

import com.web.repository.SysUserRepository;
import org.kaze.framework.ioc.annotation.Autowired;
import org.kaze.framework.ioc.stereotype.Service;

@Service
public class SysUserService {

    @Autowired
    private SysUserRepository sysUserRepository;


}
