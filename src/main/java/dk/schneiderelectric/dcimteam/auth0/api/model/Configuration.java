package dk.schneiderelectric.dcimteam.auth0.api.model;

import java.io.Serializable;
import java.util.Objects;
import java.util.Optional;

public class Configuration implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 6221168374253285406L;
	private String callbackUrl;
	private String domain;
	private String clientID;
	private String clientSecret;
	private String redirectError;
	private String redirectSuccess;

	public Configuration() {
		super();
	}

	public Configuration(ConfBuilder builder) {
		Objects.requireNonNull(builder.callbackUrl);
		Objects.requireNonNull(builder.domain);
		Objects.requireNonNull(builder.clientID);
		Objects.requireNonNull(builder.clientSecret);
		this.callbackUrl = builder.callbackUrl;
		this.domain = builder.domain;
		this.clientID = builder.clientID;
		this.clientSecret = builder.clientSecret;
		redirectError=Optional.ofNullable(builder.redirectError)
				.filter(s -> !s.isEmpty()).orElse("/login.action?os_destination=%2Findex.action&permissionViolation=true");
		redirectSuccess=Optional.ofNullable(builder.redirectSuccess)
				.filter(s -> !s.isEmpty()).orElse("/");
	}

	interface Params {
		ConfBuilder withCallbackURL(String url);

		ConfBuilder withDomain(String domain);

		ConfBuilder withClientID(String clientID);

		ConfBuilder withClientSecret(String clientSecret);

		ConfBuilder withRedirectError(String redirectError);

		ConfBuilder withRedirectSuccess(String redirectSuccess);

		Configuration build();
	}

	public static class ConfBuilder implements Params {
		private String callbackUrl;
		private String domain;
		private String clientID;
		private String clientSecret;
		private String redirectError;
		private String redirectSuccess;

		@Override
		public ConfBuilder withCallbackURL(String url) {
			this.callbackUrl = url;
			return this;
		}

		@Override
		public ConfBuilder withDomain(String domain) {
			this.domain = domain;
			return this;
		}

		@Override
		public ConfBuilder withClientID(String clientID) {
			this.clientID = clientID;
			return this;
		}

		@Override
		public ConfBuilder withClientSecret(String clientSecret) {
			this.clientSecret = clientSecret;
			return this;
		}

		@Override
		public ConfBuilder withRedirectError(String redirectError) {
			this.redirectError = redirectError;
			return this;
		}

		@Override
		public ConfBuilder withRedirectSuccess(String redirectSuccess) {
			this.redirectSuccess = redirectSuccess;
			return this;
		}

		@Override
		public Configuration build() {
			return new Configuration(this);
		}

	}

	public String getCallbackUrl() {
		return callbackUrl;
	}

	public void setCallbackUrl(String callbackUrl) {
		this.callbackUrl = callbackUrl;
	}

	public String getDomain() {
		return domain;
	}

	public void setDomain(String domain) {
		this.domain = domain;
	}

	public String getClientID() {
		return clientID;
	}

	public void setClientID(String clientID) {
		this.clientID = clientID;
	}

	public String getClientSecret() {
		return clientSecret;
	}

	public void setClientSecret(String clientSecret) {
		this.clientSecret = clientSecret;
	}

	public String getRedirectError() {
		return redirectError;
	}

	public void setRedirectError(String redirectError) {
		this.redirectError = redirectError;
	}

	public String getRedirectSuccess() {
		return redirectSuccess;
	}

	public void setRedirectSuccess(String redirectSuccess) {
		this.redirectSuccess = redirectSuccess;
	}

	@Override
	public String toString() {
		return "Configuration [callbackUrl=" + callbackUrl + ", domain=" + domain + ", clientID=" + clientID
				+ ", clientSecret=" + clientSecret + ", redirectError=" + redirectError + ", redirectSuccess="
				+ redirectSuccess + "]";
	}
}
