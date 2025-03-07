package com.wisetect.architectureservice.service;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class ArchitectureService {

    private static final int MAX_QUERY_LENGTH = 255;
}
