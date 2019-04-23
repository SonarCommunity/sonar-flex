/*
 * SonarQube Flex Plugin
 * Copyright (C) 2010-2019 SonarSource SA
 * mailto:info AT sonarsource DOT com
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
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */
package org.sonar.flex.checks;

import com.sonar.sslr.api.AstNode;
import com.sonar.sslr.api.AstNodeType;
import java.util.Collections;
import java.util.List;
import org.sonar.check.Rule;
import org.sonar.flex.FlexCheck;
import org.sonar.flex.FlexGrammar;
import org.sonar.flex.FlexKeyword;

@Rule(key = "S1066")
public class CollapsibleIfStatementCheck extends FlexCheck {

  @Override
  public List<AstNodeType> subscribedTo() {
    return Collections.singletonList(FlexGrammar.IF_STATEMENT);
  }

  @Override
  public void visitNode(AstNode astNode) {
    if (!hasElseClause(astNode)) {

      AstNode childtSatementNode = astNode.getFirstChild(FlexGrammar.SUB_STATEMENT).getFirstChild(FlexGrammar.STATEMENT);
      if (childtSatementNode != null) {

        AstNode nestedCollapsibleIf = getNestedIfCollapsible(childtSatementNode.getFirstChild());
        if (nestedCollapsibleIf != null) {
          addIssue("Merge this if statement with the enclosing one.", nestedCollapsibleIf);
        }

      }
    }
  }

  private static AstNode getNestedIfCollapsible(AstNode statementNode) {
    if (statementNode.is(FlexGrammar.IF_STATEMENT)) {
      return statementNode;
    }

    if (statementNode.is(FlexGrammar.BLOCK) && statementNode.getFirstChild(FlexGrammar.DIRECTIVES).getChildren().size() == 1) {
      AstNode singleStatementChild = statementNode.getFirstChild(FlexGrammar.DIRECTIVES).getFirstChild(FlexGrammar.DIRECTIVE).getFirstChild();

      if (singleStatementChild.is(FlexGrammar.STATEMENT) && singleStatementChild.getFirstChild().is(FlexGrammar.IF_STATEMENT)) {
        AstNode ifNode = singleStatementChild.getFirstChild();
        return !hasElseClause(ifNode) ? ifNode : null;
      }
    }
    return null;
  }

  private static boolean hasElseClause(AstNode astNode) {
    return astNode.hasDirectChildren(FlexKeyword.ELSE);
  }
}
