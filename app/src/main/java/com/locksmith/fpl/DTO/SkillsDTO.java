package com.locksmith.fpl.DTO;

import java.io.Serializable;

public class SkillsDTO implements Serializable {
        String id = "";
        String cat_id = "";
        String skill = "";
        String created_at = "";
        String updated_at = "";

        public String getId() {
                return id;
        }

        public void setId(String id) {
                this.id = id;
        }

        public String getCat_id() {
                return cat_id;
        }

        public void setCat_id(String cat_id) {
                this.cat_id = cat_id;
        }

        public String getSkill() {
                return skill;
        }

        public void setSkill(String skill) {
                this.skill = skill;
        }

        public String getCreated_at() {
                return created_at;
        }

        public void setCreated_at(String created_at) {
                this.created_at = created_at;
        }

        public String getUpdated_at() {
                return updated_at;
        }

        public void setUpdated_at(String updated_at) {
                this.updated_at = updated_at;
        }
}