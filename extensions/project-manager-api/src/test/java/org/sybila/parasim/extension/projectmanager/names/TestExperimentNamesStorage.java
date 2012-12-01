/**
 * Copyright 2011 - 2012, Sybila, Systems Biology Laboratory and individual
 * contributors by the @authors tag.
 *
 * This file is part of Parasim.
 *
 * Parasim is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.sybila.parasim.extension.projectmanager.names;

import org.sybila.parasim.extension.projectmanager.names.ExperimentNames;
import org.sybila.parasim.extension.projectmanager.names.ExperimentNamesResource;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import org.sybila.parasim.extension.projectmanager.project.ResourceException;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 *
 * @author <a href="mailto:xvejpust@fi.muni.cz">Tomáš Vejpustek</a>
 */
public class TestExperimentNamesStorage {

    private ExperimentNames getTestExperimentNames() {
        ExperimentNames result = new ExperimentNames();
        result.setModelName("testModel");
        result.setFormulaName("Tameka Sutherlin");
        result.setInitialSpaceName("Hugh Knudtson");
        result.setSimulationSpaceName("Jamie Gills");
        result.setPrecisionConfigurationName("Max Mccook");
        result.setInitialSamplingName("Odessa Juliano");
        result.setVerificationResultName("Ted Pin");
        result.setTimeoutAmount(480000);
        result.setIterationLimit(25);
        result.setAnnotation("A test experiment.");
        return result;
    }

    @Test
    public void testLoad() {
        ExperimentNamesResource resource = null;
        try {
            resource = new ExperimentNamesResource(new File(getClass().getClassLoader().getResource("testExperiment.properties").toURI()));
        } catch (URISyntaxException urise) {
            urise.printStackTrace();
            Assert.fail("Unable to find test properties file.");
        }
        try {
            resource.load();
        } catch (ResourceException re) {
            if (re.getCause() != null) {
                re.getCause().printStackTrace();
            }
            Assert.fail("Loading error: " + re.getMessage());
        }
        Assert.assertEquals(resource.getRoot(), getTestExperimentNames(), "Experiment names should be loaded correctly.");
    }

    @Test
    public void testStoreLoad() {
        File temp = null;
        try {
            temp = File.createTempFile("experiment", ".properties");
        } catch (IOException ioe) {
            ioe.printStackTrace();
            Assert.fail("Unable to create temporary file.");
        }

        //store
        ExperimentNamesResource resource = new ExperimentNamesResource(temp);
        resource.setRoot(getTestExperimentNames());
        try {
            resource.store();
        } catch (ResourceException re) {
            if (re.getCause() != null) {
                re.getCause().printStackTrace();
            }
            Assert.fail("Storing error: " + re.getMessage());
        }

        //load
        resource.setRoot(null);
        try {
            resource.load();
        } catch (ResourceException re) {
            if (re.getCause() != null) {
                re.getCause().printStackTrace();
            }
            Assert.fail("Load error: " + re.getMessage());
        }

        Assert.assertEquals(resource.getRoot(), getTestExperimentNames(), "After being stored and loaded the experiment names should not change.");
    }
}
