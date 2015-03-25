package org.swistowski.dvh;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import org.swistowski.dvh.models.Character;

import java.util.ArrayList;
import java.util.List;

class ItemsFragmentPagerAdapter extends FragmentStatePagerAdapter {
    private final List<Page> mPages = new ArrayList<Page>();

    public ItemsFragmentPagerAdapter(FragmentManager fm) {
        super(fm);
        if (Database.getInstance().getCharacters() != null) {
            for (final Character character : Database.getInstance().getCharacters()) {
                mPages.add(new Page(character.toString(), new FragmentProvider() {
                    @Override
                    public Fragment getFragment() {
                        return ItemListFragment.newInstance(ItemListFragment.DIRECTION_TO, character.getId());
                    }
                }));
                /*
                  Future functionality, move from character too
                "▼ " +
                mPages.add(new Page("▲ " + character.toString(), new FragmentProvider() {
                    @Override
                    public Fragment getFragment() {
                        return ItemListFragment.newInstance(ItemListFragment.DIRECTION_FROM, character.getId());
                    }
                }));
                */
            }
        }

        if (Database.getInstance().getItems().size() > 0) {
            mPages.add(new Page("Vault", new FragmentProvider() {

                @Override
                public Fragment getFragment() {
                    return ItemListFragment.newInstance(ItemListFragment.DIRECTION_TO, Database.VAULT_ID);
                }
            }));
            /*
                Future functionality, move from vault to
            mPages.add(new MyPage(MyPage.VAULT, "▲ Vault", new FragmentProvider() {
                @Override
                public Fragment getFragment() {
                    return ItemListFragment.newInstance(ItemListFragment.DIRECTION_FROM, Database.VAULT_ID);
                }
            }));
            */
        }
        if(mPages.size()==0) {
            mPages.add(new Page("Setting", new FragmentProvider() {
                @Override
                public Fragment getFragment() {
                    return SettingsFragment.newInstance();
                }
            }));
        }
    }

    @Override
    public Fragment getItem(int i) {
        return mPages.get(i).getFragment();
    }

    @Override
    public int getCount() {
        return mPages.size();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return mPages.get(position).getLabel();
    }

    private interface FragmentProvider {
        public Fragment getFragment();
    }

    private class Page {
        private final CharSequence label;
        private final FragmentProvider fp;

        public Page(CharSequence label, FragmentProvider fp) {
            this.label = label;
            this.fp = fp;
        }

        public CharSequence getLabel() {
            return label;
        }

        public Fragment getFragment() {
            return fp.getFragment();
        }
    }
}
