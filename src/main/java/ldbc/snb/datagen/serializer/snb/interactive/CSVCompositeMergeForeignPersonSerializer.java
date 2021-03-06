/* 
 Copyright (c) 2013 LDBC
 Linked Data Benchmark Council (http://www.ldbcouncil.org)
 
 This file is part of ldbc_snb_datagen.
 
 ldbc_snb_datagen is free software: you can redistribute it and/or modify
 it under the terms of the GNU General Public License as published by
 the Free Software Foundation, either version 3 of the License, or
 (at your option) any later version.
 
 ldbc_snb_datagen is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 GNU General Public License for more details.
 
 You should have received a copy of the GNU General Public License
 along with ldbc_snb_datagen.  If not, see <http://www.gnu.org/licenses/>.
 
 Copyright (C) 2011 OpenLink Software <bdsmt@openlinksw.com>
 All Rights Reserved.
 
 This program is free software; you can redistribute it and/or modify
 it under the terms of the GNU General Public License as published by
 the Free Software Foundation;  only Version 2 of the License dated
 June 1991.
 
 This program is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 GNU General Public License for more details.
 
 You should have received a copy of the GNU General Public License
 along with this program; if not, write to the Free Software
 Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.*/
package ldbc.snb.datagen.serializer.snb.interactive;

import ldbc.snb.datagen.dictionary.Dictionaries;
import ldbc.snb.datagen.objects.Knows;
import ldbc.snb.datagen.objects.Person;
import ldbc.snb.datagen.objects.StudyAt;
import ldbc.snb.datagen.objects.WorkAt;
import ldbc.snb.datagen.serializer.HDFSCSVWriter;
import ldbc.snb.datagen.serializer.PersonSerializer;
import org.apache.hadoop.conf.Configuration;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;

/**
 * Created by aprat on 17/02/15.
 */
public class CSVCompositeMergeForeignPersonSerializer extends PersonSerializer {

    private HDFSCSVWriter[] writers;

    private enum FileNames {
        PERSON("person"),
        PERSON_HAS_INTEREST_TAG("person_hasInterest_tag"),
        PERSON_WORK_AT("person_workAt_organisation"),
        PERSON_STUDY_AT("person_studyAt_organisation"),
        PERSON_KNOWS_PERSON("person_knows_person");

        private final String name;

        private FileNames(String name) {
            this.name = name;
        }

        public String toString() {
            return name;
        }
    }

    public void initialize(Configuration conf, int reducerId) throws IOException {
        int numFiles = FileNames.values().length;
        writers = new HDFSCSVWriter[numFiles];
        for (int i = 0; i < numFiles; ++i) {
            writers[i] = new HDFSCSVWriter(conf.get("ldbc.snb.datagen.serializer.socialNetworkDir"), FileNames
                    .values()[i].toString() + "_" + reducerId, conf.getInt("ldbc.snb.datagen.numPartitions", 1), conf
                                                   .getBoolean("ldbc.snb.datagen.serializer.compressed", false), "|", conf
                                                   .getBoolean("ldbc.snb.datagen.serializer.endlineSeparator", false));
        }

        ArrayList<String> arguments = new ArrayList<String>();
        arguments.add("id");
        arguments.add("firstName");
        arguments.add("lastName");
        arguments.add("gender");
        arguments.add("birthday");
        arguments.add("creationDate");
        arguments.add("locationIP");
        arguments.add("browserUsed");
        arguments.add("place");
        arguments.add("language");
        arguments.add("email");
        writers[FileNames.PERSON.ordinal()].writeHeader(arguments);

        arguments.clear();
        arguments.add("Person.id");
        arguments.add("Tag.id");
        writers[FileNames.PERSON_HAS_INTEREST_TAG.ordinal()].writeHeader(arguments);

        arguments.clear();
        arguments.add("Person.id");
        arguments.add("Organisation.id");
        arguments.add("workFrom");
        writers[FileNames.PERSON_WORK_AT.ordinal()].writeHeader(arguments);

        arguments.clear();
        arguments.add("Person.id");
        arguments.add("Organisation.id");
        arguments.add("classYear");
        writers[FileNames.PERSON_STUDY_AT.ordinal()].writeHeader(arguments);

        arguments.clear();
        arguments.add("Person.id");
        arguments.add("Person.id");
        arguments.add("creationDate");
        writers[FileNames.PERSON_KNOWS_PERSON.ordinal()].writeHeader(arguments);

    }

    @Override
    public void close() {
        int numFiles = FileNames.values().length;
        for (int i = 0; i < numFiles; ++i) {
            writers[i].close();
        }
    }

    @Override
    protected void serialize(final Person p) {

        ArrayList<String> arguments = new ArrayList<String>();

        arguments.add(Long.toString(p.accountId()));
        arguments.add(p.firstName());
        arguments.add(p.lastName());
        if (p.gender() == 1) {
            arguments.add("male");
        } else {
            arguments.add("female");
        }

        String dateString = Dictionaries.dates.formatDate(p.birthday());
        arguments.add(dateString);

        dateString = Dictionaries.dates.formatDateTime(p.creationDate());
        arguments.add(dateString);
        arguments.add(p.ipAddress().toString());
        arguments.add(Dictionaries.browsers.getName(p.browserId()));
        arguments.add(Integer.toString(p.cityId()));

        ArrayList<Integer> languages = p.languages();
        StringBuilder languagesBuilder = new StringBuilder();
        for (int i = 0; i < languages.size()-1; i++) {
            languagesBuilder.append(Dictionaries.languages.getLanguageName(languages.get(i))+";");
        }
        if(languages.size() > 0) {
            languagesBuilder.append(Dictionaries.languages.getLanguageName(languages.get(languages.size()-1)));
        }
        arguments.add(languagesBuilder.toString());

        StringBuilder emailsBuilder = new StringBuilder();
        Iterator<String> itString = p.emails().iterator();
        for (int i = 0; i < p.emails().size()-1; i++) {
            emailsBuilder.append(itString.next()+";");
        }
        if(itString.hasNext()) {
            emailsBuilder.append(itString.next());
        }
        arguments.add(emailsBuilder.toString());
        writers[FileNames.PERSON.ordinal()].writeEntry(arguments);

        Iterator<Integer> itInteger = p.interests().iterator();
        while (itInteger.hasNext()) {
            arguments.clear();
            Integer interestIdx = itInteger.next();
            arguments.add(Long.toString(p.accountId()));
            arguments.add(Integer.toString(interestIdx));
            writers[FileNames.PERSON_HAS_INTEREST_TAG.ordinal()].writeEntry(arguments);
        }
    }

    @Override
    protected void serialize(final StudyAt studyAt) {
        ArrayList<String> arguments = new ArrayList<String>();
        String dateString = Dictionaries.dates.formatYear(studyAt.year);
        arguments.add(Long.toString(studyAt.user));
        arguments.add(Long.toString(studyAt.university));
        arguments.add(dateString);
        writers[FileNames.PERSON_STUDY_AT.ordinal()].writeEntry(arguments);
    }

    @Override
    protected void serialize(final WorkAt workAt) {
        ArrayList<String> arguments = new ArrayList<String>();
        String dateString = Dictionaries.dates.formatYear(workAt.year);
        arguments.add(Long.toString(workAt.user));
        arguments.add(Long.toString(workAt.company));
        arguments.add(dateString);
        writers[FileNames.PERSON_WORK_AT.ordinal()].writeEntry(arguments);
    }

    @Override
    protected void serialize(final Person p, Knows knows) {
        ArrayList<String> arguments = new ArrayList<String>();
        String dateString = Dictionaries.dates.formatDateTime(knows.creationDate());
        arguments.add(Long.toString(p.accountId()));
        arguments.add(Long.toString(knows.to().accountId()));
        arguments.add(dateString);
        writers[FileNames.PERSON_KNOWS_PERSON.ordinal()].writeEntry(arguments);
    }

    @Override
    public void reset() {
        // Intentionally left empty
    }
}
