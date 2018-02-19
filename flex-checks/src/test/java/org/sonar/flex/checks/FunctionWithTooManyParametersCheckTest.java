/*
 * SonarQube Flex Plugin
 * Copyright (C) 2010-2018 SonarSource SA
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

import org.junit.Test;
import org.sonar.squidbridge.checks.CheckMessagesVerifier;

import java.io.File;

public class FunctionWithTooManyParametersCheckTest {

  private FunctionWithTooManyParametersCheck check = new FunctionWithTooManyParametersCheck();

  @Test
  public void defaults() {
    CheckMessagesVerifier.verify(FlexCheckTester.checkMessages(new File("src/test/resources/checks/FunctionWithTooManyParameters.as"), check))
      .next().atLine(4).withMessage("This function has 8 parameters, which is greater than the 7 authorized.")
      .noMore();
  }

  @Test
  public void custom() {
    check.max = 4;

    CheckMessagesVerifier.verify(FlexCheckTester.checkMessages(new File("src/test/resources/checks/FunctionWithTooManyParameters.as"), check))
      .next().atLine(1).withMessage("This function has 7 parameters, which is greater than the 4 authorized.")
      .next().atLine(4).withMessage("This function has 8 parameters, which is greater than the 4 authorized.")
      .noMore();
  }

}
