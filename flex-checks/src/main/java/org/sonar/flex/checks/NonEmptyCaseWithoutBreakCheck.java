/*
 * SonarQube Flex Plugin
 * Copyright (C) 2010-2021 SonarSource SA
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

@Rule(key = "NonEmptyCaseWithoutBreak")
public class NonEmptyCaseWithoutBreakCheck extends FlexCheck {

  private static final AstNodeType[] JUMP_NODES = {FlexGrammar.BREAK_STATEMENT, FlexGrammar.RETURN_STATEMENT, FlexGrammar.THROW_STATEMENT, FlexGrammar.CONTINUE_STATEMENT};

  @Override
  public List<AstNodeType> subscribedTo() {
    return Collections.singletonList(FlexGrammar.CASE_ELEMENT);
  }

  @Override
  public void visitNode(AstNode astNode) {
    if (isLastCaseElement(astNode)) {
      return;
    }

    int jumpStmtNumber = astNode.getDescendants(JUMP_NODES).size();

    if (jumpStmtNumber < 2) {
      AstNode directive = astNode.getLastChild();
      visitLastDirective(astNode, directive);
    }
  }

  private static boolean isLastCaseElement(AstNode astNode) {
    return astNode.getNextSibling().isNot(FlexGrammar.CASE_ELEMENT);
  }

  private void visitLastDirective(AstNode astNode, AstNode directive) {
    if (isBlock(directive)) {
      visitLastDirective(astNode, directive.getFirstChild().getFirstChild().getLastChild());
      return;
    }
    if (directive.getFirstChild().is(FlexGrammar.STATEMENT)
      && directive.getFirstChild().getFirstChild().isNot(JUMP_NODES)) {
      List<AstNode> children = astNode.getChildren(FlexGrammar.CASE_LABEL);
      addIssue("Last statement in this switch-clause should be an unconditional break.",
        children.get(children.size() - 1));
    }
  }

  private static boolean isBlock(AstNode directive) {
    return directive.getNumberOfChildren() == 1
        && directive.getFirstChild().is(FlexGrammar.STATEMENT)
        && directive.getFirstChild().getNumberOfChildren() == 1
        && directive.getFirstChild().getFirstChild().is(FlexGrammar.BLOCK);
  }

}
