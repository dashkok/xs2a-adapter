/*
 * Copyright 2018-2018 adorsys GmbH & Co KG
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package de.adorsys.xs2a.tpp;

import de.adorsys.xs2a.adapter.api.remote.AccountInformationClient;
import de.adorsys.xs2a.adapter.api.remote.PaymentInitiationClient;
import de.adorsys.xs2a.adapter.mapper.HeadersMapper;
import de.adorsys.xs2a.adapter.mapper.PaymentInitiationScaStatusResponseMapper;
import de.adorsys.xs2a.adapter.service.PaymentInitiationService;
import de.adorsys.xs2a.adapter.service.ais.AccountInformationService;
import de.adorsys.xs2a.adapter.service.impl.AccountInformationServiceImpl;
import de.adorsys.xs2a.adapter.service.impl.PaymentInitiationServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AppConfiguration {

    @Autowired
    AccountInformationClient accountInformationClient;

    @Autowired
    PaymentInitiationClient paymentInitiationClient;

    @Bean
    PaymentInitiationScaStatusResponseMapper getPaymentInitiationScaStatusResponseMapper() {
        return new PaymentInitiationScaStatusResponseMapper();
    }

    @Bean
    HeadersMapper getHeadersMapper() {
        return new HeadersMapper();
    }

    @Bean
    PaymentInitiationService paymentInitiationService() {
        return new PaymentInitiationServiceImpl(paymentInitiationClient);
    }

    @Bean
    AccountInformationService accountInformationService() {
        return new AccountInformationServiceImpl(accountInformationClient);
    }
}