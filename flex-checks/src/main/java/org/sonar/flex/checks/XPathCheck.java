/*
 * SonarQube Flex Plugin
 * Copyright (C) 2010-2024 SonarSource SA
 * mailto:info AT sonarsource DOT com
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the Sonar Source-Available License Version 1, as published by SonarSource SA.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the Sonar Source-Available License for more details.
 *
 * You should have received a copy of the Sonar Source-Available License
 * along with this program; if not, see https://sonarsource.com/license/ssal/
 */
package org.sonar.flex.checks;

import com.sonar.sslr.api.AstNode;
import com.sonar.sslr.api.AstNodeType;
import com.sonar.sslr.xpath.api.AstNodeXPathQuery;
import java.util.Collections;
import java.util.List;
import javax.annotation.CheckForNull;
import javax.annotation.Nullable;
import org.sonar.check.Rule;
import org.sonar.check.RuleProperty;
import org.sonar.flex.FlexCheck;

@Rule(key = "XPath")
public class XPathCheck extends FlexCheck {

  private static final String DEFAULT_XPATH_QUERY = "";
  private static final String DEFAULT_MESSAGE = "The XPath expression matches this piece of code";

  @RuleProperty(
    key = "xpathQuery",
    description = "The XPath query",
    defaultValue = "" + DEFAULT_XPATH_QUERY,
    type = "TEXT")
  public String xpathQuery = DEFAULT_XPATH_QUERY;

  @RuleProperty(
    key = "message",
    description = "The issue message",
    defaultValue = "" + DEFAULT_MESSAGE)
  public String message = DEFAULT_MESSAGE;

  private AstNodeXPathQuery<Object> query = null;

  @Override
  public List<AstNodeType> subscribedTo() {
    return Collections.emptyList();
  }

  @CheckForNull
  public AstNodeXPathQuery<Object> query() {
    if (query == null && !(xpathQuery == null || xpathQuery.isEmpty())) {
      try {
        query = AstNodeXPathQuery.create(xpathQuery);
      } catch (RuntimeException e) {
        throw new IllegalStateException("Unable to initialize the XPath engine, perhaps because of an invalid query: " + xpathQuery, e);
      }
    }
    return query;
  }

  @Override
  public void visitFile(@Nullable AstNode fileNode) {
    AstNodeXPathQuery<Object> xpath = query();
    if (xpath != null && fileNode != null) {
      for (Object object : xpath.selectNodes(fileNode)) {
        if (object instanceof AstNode) {
          AstNode astNode = (AstNode) object;
          addIssueAtLine(message, astNode.getTokenLine());
        } else if (Boolean.TRUE.equals(object)) {
          addFileIssue(message);
        }
      }
    }
  }
}
