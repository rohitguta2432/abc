package com.socioseer.common.kafka.serializer;

import java.util.Map;

import org.apache.kafka.common.config.AbstractConfig;
import org.apache.kafka.common.config.ConfigDef;

/**
 * @author OrangeMantra
 * @since JDK 1.8
 * @version 1.0
 *
 */
public class KafkaJsonSerializerConfig extends AbstractConfig {


  public static final String JSON_INDENT_OUTPUT = "json.indent.output";
  public static final boolean JSON_INDENT_OUTPUT_DEFAULT = false;
  public static final String JSON_INDENT_OUTPUT_DOC =
      "Whether JSON output should be indented (\"pretty-printed\")";

  private static ConfigDef config;

  static {
    config =
        new ConfigDef().define(JSON_INDENT_OUTPUT, ConfigDef.Type.BOOLEAN,
            JSON_INDENT_OUTPUT_DEFAULT, ConfigDef.Importance.LOW, JSON_INDENT_OUTPUT_DOC);
  }


  public KafkaJsonSerializerConfig(Map<?, ?> originals) {
    super(config, originals);
  }

}
