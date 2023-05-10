import difflib
import jellyfish

def get_closest_match(word, candidates):
    # Use difflib to get the five closest matches based on substring similarity
    matches = difflib.get_close_matches(word, candidates, n=5, cutoff=0.4)
    
    # Compute the Damerau-Levenshtein and Jaro-Winkler distances for the matches
    dl_distances = [jellyfish.damerau_levenshtein_distance(word, match) for match in matches]
    jw_distances = [jellyfish.jaro_winkler(word, match) for match in matches]
    
    # Combine the distances into a single score using a weighted sum
    scores = [0.5 * dl + 0.5 * jw for dl, jw in zip(dl_distances, jw_distances)]
    
    # Return the match with the smallest score
    min_score_index = scores.index(min(scores))
    return matches[min_score_index]

candidates = ["SDPL039", "OP13006", 'PGL3100']
word = 'OPL3OOG'
closest_match = get_closest_match(word, candidates)
print(f"Closest match to {word} is {closest_match}")
