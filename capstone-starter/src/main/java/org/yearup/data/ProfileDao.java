package org.yearup.data;


import org.yearup.models.Profile;

public interface ProfileDao
{
    Profile create(Profile profile);
    Profile profileById(int id);
    Profile updateProfile(int id, Profile profile);
}
