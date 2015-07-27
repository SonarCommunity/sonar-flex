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
package org.sonar.flex.checks;

import com.sonar.sslr.api.AstNode;
import org.sonar.check.Priority;
import org.sonar.check.Rule;
import org.sonar.flex.FlexGrammar;
import org.sonar.flex.checks.utils.Expression;
import org.sonar.squidbridge.checks.SquidCheck;
import org.sonar.sslr.parser.LexerlessGrammar;

@Rule(
  key = "S1982",
  priority = Priority.MAJOR)
public class OnEnterFrameUseCheck extends SquidCheck<LexerlessGrammar> {

  @Override
  public void init() {
    subscribeTo(FlexGrammar.ASSIGNMENT_EXPR);
  }

  @Override
  public void visitNode(AstNode astNode) {
    if (astNode.getNumberOfChildren() > 1 && isOnEnterFrame(astNode.getFirstChild()) && isFunctionExpr(astNode.getLastChild())) {
      getContext().createLineViolation(this, "Refactor this code to remove the use of \"onEnterFrame\" event handler.", astNode);
    }
  }

  private boolean isFunctionExpr(AstNode assignementExpr) {
    AstNode assignmentExprChild = assignementExpr.getFirstChild();
    return assignmentExprChild.is(FlexGrammar.POSTFIX_EXPR) && assignmentExprChild.getFirstChild().getFirstChild().is(FlexGrammar.FUNCTION_EXPR);
  }

  private boolean isOnEnterFrame(AstNode postfixExpr) {
    return Expression.exprToString(postfixExpr).endsWith(".onEnterFrame");
  }

}
