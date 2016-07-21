package org.openlmis.sms.Repository.mapper;

import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

@Repository
public interface ConfigMapper {

  // Used by mapper
  @Select("SELECT value FROM configuration_settings WHERE LOWER(key) = LOWER(#{key}) limit 1")
  String getValueByKey(String key);

}
