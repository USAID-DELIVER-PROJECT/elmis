/*
 * Electronic Logistics Management Information System (eLMIS) is a supply chain management system for health commodities in a developing country setting.
 *
 * Copyright (C) 2015  John Snow, Inc (JSI). This program was produced for the U.S. Agency for International Development (USAID). It was prepared under the USAID | DELIVER PROJECT, Task Order 4.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.openlmis.config;

import com.fasterxml.classmate.TypeResolver;
import org.openlmis.core.web.OpenLmisResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.context.request.async.DeferredResult;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.builders.ResponseMessageBuilder;
import springfox.documentation.schema.ModelRef;
import springfox.documentation.service.Tag;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.lang.reflect.WildcardType;
import java.time.LocalDate;

import static springfox.documentation.schema.AlternateTypeRules.newRule;

@EnableSwagger2
public class MySwaggerConfig {

  private static Docket docket;

  @Autowired
  TypeResolver typeResolver;

  @Bean
  public Docket elmisApi() {
    if (docket == null) {
      docket = new Docket(DocumentationType.SWAGGER_2)
          .select()
          .apis(RequestHandlerSelectors.any())
          .paths(PathSelectors.ant("/rest-api/**"))
          .build()
          .directModelSubstitute(LocalDate.class,
              String.class)
          .genericModelSubstitutes(ResponseEntity.class)
          .alternateTypeRules(
              newRule(typeResolver.resolve(DeferredResult.class,
                  typeResolver.resolve(ResponseEntity.class, WildcardType.class)),
                  typeResolver.resolve(WildcardType.class)))
          .useDefaultResponseMessages(false)
          .globalResponseMessage(RequestMethod.GET,
              java.util.Arrays.asList(new ResponseMessageBuilder()
                  .code(500)
                  .message("500 message")
                  .responseModel(new ModelRef("Error"))
                  .build()))
          .enableUrlTemplating(false)
          .tags(new Tag("ELMIS APIs", "APIs related to eLMIS"))
      ;
    }
    return docket;
  }

}
