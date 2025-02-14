/*
 * SonarQube Flex Plugin
 * Copyright (C) 2010-2025 SonarSource SA
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
import java.util.Arrays;
import java.util.List;
import org.sonar.check.Rule;
import org.sonar.flex.FlexCheck;
import org.sonar.flex.FlexGrammar;

@Rule(key = "FunctionSinglePointOfExit")
public class FunctionSinglePointOfExitCheck extends FlexCheck {

  private int returnStatements;

  @Override
  public List<AstNodeType> subscribedTo() {
    return Arrays.asList(FlexGrammar.FUNCTION_DEF, FlexGrammar.RETURN_STATEMENT);
  }

  @Override
  public void visitNode(AstNode node) {
    if (node.is(FlexGrammar.FUNCTION_DEF)) {
      returnStatements = 0;
    } else if (node.is(FlexGrammar.RETURN_STATEMENT)) {
      returnStatements++;
    }
  }

  @Override
  public void leaveNode(AstNode node) {
    if (node.is(FlexGrammar.FUNCTION_DEF) && (returnStatements != 0) && (returnStatements > 1 || !hasReturnAtEnd(node))) {
      addIssue("A function shall have a single point of exit at the end of the function.", node);
    }
  }

  private static boolean hasReturnAtEnd(AstNode functionDefinitionNode) {
    AstNode lastDirectiveNode = functionDefinitionNode
      .getFirstChild(FlexGrammar.FUNCTION_COMMON)
      .getFirstChild(FlexGrammar.BLOCK)
      .getFirstChild(FlexGrammar.DIRECTIVES)
      .getLastChild();
    if (lastDirectiveNode != null) {
      AstNode statementNode = lastDirectiveNode.getFirstChild(FlexGrammar.STATEMENT);
      if (statementNode != null && statementNode.getFirstChild().is(FlexGrammar.RETURN_STATEMENT)) {
        return true;
      }
    }
    return false;
  }

}
