package requirements;

import java.util.ArrayList;
import org.json.JSONArray;
import org.json.JSONObject;

public class RequirementFactory {
    public static Requirement buildRequirement(JSONObject jsonReq) {
        String type = jsonReq.getString("type");
        JSONArray jRequirements = jsonReq.optJSONArray("req");
        JSONObject jRequirement = jsonReq.optJSONObject("req");
        String requirementStr = jsonReq.optString("req");
        ArrayList<Requirement> requirements = new ArrayList<>();

        switch (type) {
            case "perk":
                return new PerkRequirement(requirementStr);

            case "item":
                return new ItemRequirement(requirementStr);

            case "status":
                return new StatusRequirement(requirementStr);

            case "or":
                jRequirements.forEach(jReq -> requirements.add(buildRequirement((JSONObject) jReq)));
                return new OrRequirement(requirements);

            case "and":
                jRequirements.forEach(jReq -> requirements.add(buildRequirement((JSONObject) jReq)));
                return new AndRequirement(requirements);

            case "not":
                return new NotRequirement(buildRequirement(jRequirement));
        }

        return null;
    }
}
