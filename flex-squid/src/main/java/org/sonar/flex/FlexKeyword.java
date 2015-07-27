/*
 * SonarQube Flex Plugin
 * Copyright (C) 2010 SonarSource
 * sonarqube@googlegroups.com
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02
 */
package org.sonar.flex;

import com.google.common.collect.ImmutableList;
import org.sonar.sslr.grammar.GrammarRuleKey;

import java.util.List;

public enum FlexKeyword implements GrammarRuleKey {

  /**
   * "as" is not keyword in ActionScript 2, so we treat it as syntactic keyword
   */
  AS(true),
  BREAK,
  CASE,
  CATCH,
  CLASS,
  CONST,
  CONTINUE,
  DEFAULT,
  DELETE,
  DO,
  ELSE,
  EXTENDS,
  FALSE,
  FINALLY,
  FOR,
  FUNCTION,
  IF,
  IMPLEMENTS,
  IMPORT,
  IN,
  INSTANCEOF,
  INTERFACE,
  INTERNAL,
  IS,
  NATIVE,
  NEW,
  NULL,
  PACKAGE,
  PRIVATE,
  PROTECTED,
  PUBLIC,
  RETURN,
  SUPER,
  SWITCH,
  THIS,
  THROW,
  TRUE,
  TRY,
  TYPEOF,
  USE,
  VAR,
  VOID,
  WHILE,
  WITH,

  EACH(true),
  GET(true),
  SET(true),
  NAMESPACE(true),
  INCLUDE(true),
  DYNAMIC(true),
  FINAL(true),
  OVERRIDE(true),
  STATIC(true),
  XML(true);

  private final boolean syntactic;

  private FlexKeyword() {
    this(false);
  }

  private FlexKeyword(boolean syntactic) {
    this.syntactic = syntactic;
  }

  public static String[] keywordValues() {
    String[] keywordsValue = new String[FlexKeyword.values().length];
    int i = 0;
    for (FlexKeyword keyword : FlexKeyword.values()) {
      keywordsValue[i] = keyword.getValue();
      i++;
    }
    return keywordsValue;
  }

  public static List<FlexKeyword> keywords() {
    ImmutableList.Builder<FlexKeyword> keywords = ImmutableList.builder();
    for (FlexKeyword keyword : values()) {
      if (!keyword.syntactic) {
        keywords.add(keyword);
      }
    }
    return keywords.build();
  }

  public String getValue() {
    return toString().toLowerCase();
  }

}
