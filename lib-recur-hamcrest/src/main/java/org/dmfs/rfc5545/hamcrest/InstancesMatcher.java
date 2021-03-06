/*
 * Copyright 2018 Marten Gajda <marten@dmfs.org>
 *
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.dmfs.rfc5545.hamcrest;

import org.dmfs.rfc5545.DateTime;
import org.dmfs.rfc5545.recur.RecurrenceRule;
import org.dmfs.rfc5545.recur.RecurrenceRuleIterator;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeDiagnosingMatcher;


/**
 * A {@link Matcher} which checks the instances generated by a {@link RecurrenceRule}.
 *
 * @author Marten Gajda
 */
public final class InstancesMatcher extends TypeSafeDiagnosingMatcher<RecurrenceRule>
{
    private final static int MAX_ITERATIONS = 10000;
    private final DateTime mStart;
    private final Matcher<DateTime> mInstancesMatcher;


    public InstancesMatcher(DateTime start, Matcher<DateTime> instancesMatcher)
    {
        mStart = start;
        mInstancesMatcher = instancesMatcher;
    }


    public static Matcher<RecurrenceRule> instances(DateTime start, Matcher<DateTime> instancesMatcher)
    {
        return new InstancesMatcher(start, instancesMatcher);
    }


    @Override
    protected boolean matchesSafely(RecurrenceRule recurrenceRule, Description mismatchDescription)
    {
        int count = 0;
        RecurrenceRuleIterator it = recurrenceRule.iterator(mStart);
        while (it.hasNext())
        {
            count++;
            DateTime instance = it.nextDateTime();
            if (!mInstancesMatcher.matches(instance))
            {
                mismatchDescription.appendText(String.format("instance %s ", instance.toString()));
                mInstancesMatcher.describeMismatch(instance, mismatchDescription);
                return false;
            }

            if (count == MAX_ITERATIONS || instance.getYear() > 9000)
            {
                break;
            }
        }
        return true;
    }


    @Override
    public void describeTo(Description description)
    {
        description.appendText("instances ");
        mInstancesMatcher.describeTo(description);
    }
}
